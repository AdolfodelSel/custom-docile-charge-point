import java.time.ZonedDateTime
import chargepoint.docile.dsl._
import com.thenewmotion.ocpp.messages.v1x._
import scala.concurrent.duration._
import scala.util._
import scala.io.StdIn.{readLine,readInt}


say("Simulate active connection")
say("Sleep 5s")
sleep(5.seconds)
statusNotification(scope = ConnectorScope(-1))
val valueMeterStart: Int = 300
val valueMeterStop: Int = 303
var valueEnergy: Int = 300
var valuePower: Int = 200
var valueInMessage: Int = 0
var counter: Int = 0;

while(true){
    val readValue = readLine("Type? E / W. Default Energy")
    val isPhase = readLine("Phase? Y / N. Default No")
    valueInMessage = if (readValue == "W") valuePower else valueEnergy
    val measurandType = if (readValue == "W") com.thenewmotion.ocpp.messages.v1x.meter.Measurand.PowerActiveImport else com.thenewmotion.ocpp.messages.v1x.meter.Measurand.EnergyActiveImportRegister
    val measurandUnit = if (readValue == "W") com.thenewmotion.ocpp.messages.v1x.meter.UnitOfMeasure.W else com.thenewmotion.ocpp.messages.v1x.meter.UnitOfMeasure.Wh
    say(s"Options -> readValue: $readValue, isPhase: $isPhase, measurandType: $measurandType, measurandUnit: $measurandUnit, valueInMessage: $valueInMessage")
    say("Waiting for remote start message")
    val noTimeout: AwaitTimeout = InfiniteAwaitTimeout
    val startRequest = expectIncoming(remoteStartTransactionReq.respondingWith(RemoteStartTransactionRes(true)))(noTimeout)
    val chargeTokenId = startRequest.idTag

    say("Received remote start, authorizing...")
    val auth = authorize(chargeTokenId).idTag

    if (auth.status == AuthorizationStatus.Accepted) {
      say("Obtained authorization from Central System; starting transaction")

      statusNotification(status = ChargePointStatus.Occupied(Some(OccupancyKind.Preparing)))
      val transId = startTransaction(meterStart = valueMeterStart, idTag = chargeTokenId).transactionId
      statusNotification(status = ChargePointStatus.Occupied(Some(OccupancyKind.Charging)))

      say(s"Transaction started with ID $transId; awaiting remote stop")

      val stopTimeout = AwaitTimeoutInMillis(10.seconds.toMillis.toInt)
      def waitForValidRemoteStop(): Unit =
        Try(
          expectIncoming(
            requestMatching({
              case r: RemoteStopTransactionReq => r.transactionId == transId
            })
              .respondingWith(RemoteStopTransactionRes(_))
          )(stopTimeout)
        ) match {
          case Success(_) =>
            say("Received RemoteStopTransaction request; stopping transaction")
            ()
          case Failure(ExpectationFailed(exc)) if exc.startsWith("Expected message not received after") =>
            say(s"Received no RemoteStopTransaction within ${stopTimeout.toDuration}; stopping transaction")
            send(
              MeterValuesReq(
                scope = ConnectorScope(0),
                transactionId = Option(transId),
                meters = List(
                  meter.Meter(
                    timestamp = ZonedDateTime.now(),
                    values = List(
                      meter.Value(
                        value = valueInMessage.toString,
                        context = meter.ReadingContext.SamplePeriodic,
                        format = meter.ValueFormat.Raw,
                        measurand = measurandType,
                        phase = None,
                        location = meter.Location.Outlet,
                        unit = measurandUnit
                      )
                    )
                  )
                )
              )
            )
            if (isPhase == "Y") {
                send(
                  MeterValuesReq(
                    scope = ConnectorScope(0),
                    transactionId = Option(transId),
                    meters = List(
                      meter.Meter(
                        timestamp = ZonedDateTime.now(),
                        values = List(
                          meter.Value(
                            value = valueInMessage.toString,
                            context = meter.ReadingContext.SamplePeriodic,
                            format = meter.ValueFormat.Raw,
                            measurand = measurandType,
                            phase = Option.apply(meter.Phase.L1),
                            location = meter.Location.Outlet,
                            unit = measurandUnit
                          )
                        )
                      )
                    )
                  )
                )
                send(
                  MeterValuesReq(
                    scope = ConnectorScope(0),
                    transactionId = Option(transId),
                    meters = List(
                      meter.Meter(
                        timestamp = ZonedDateTime.now(),
                        values = List(
                          meter.Value(
                            value = "0",
                            context = meter.ReadingContext.SamplePeriodic,
                            format = meter.ValueFormat.Raw,
                            measurand = measurandType,
                            phase = Option.apply(meter.Phase.L2),
                            location = meter.Location.Outlet,
                            unit = measurandUnit
                          )
                        )
                      )
                    )
                  )
                )
                send(
                  MeterValuesReq(
                    scope = ConnectorScope(0),
                    transactionId = Option(transId),
                    meters = List(
                      meter.Meter(
                        timestamp = ZonedDateTime.now(),
                        values = List(
                          meter.Value(
                            value = "0",
                            context = meter.ReadingContext.SamplePeriodic,
                            format = meter.ValueFormat.Raw,
                            measurand = measurandType,
                            phase = Option.apply(meter.Phase.L3),
                            location = meter.Location.Outlet,
                            unit = measurandUnit
                          )
                        )
                      )
                    )
                  )
                )
            }
            if (counter != 3) {
                counter += 1
                valueEnergy += 1
                valueInMessage = if (readValue == "W") valuePower else valueEnergy
                say(s"Updated values. Counter: $counter, valueEnergy: $valueEnergy, valueInMessage: $valueInMessage.")
                waitForValidRemoteStop()
            } else {
                say("Waiting forever for the RemoteStopTransaction request")
                waitInfiniteForValidRemoteStop()
            }
          case Failure(ExpectationFailed(_)) =>
            say(s"Received RemoteStopTransaction request for other transaction with ID. I'll keep waiting for a stop for $transId.")
            waitForValidRemoteStop()
        }

      def waitInfiniteForValidRemoteStop(): Unit =
        Try(
          expectIncoming(
            requestMatching({
              case r: RemoteStopTransactionReq => r.transactionId == transId
            })
              .respondingWith(RemoteStopTransactionRes(_))
          )(noTimeout)
        ) match {
          case Success(_) =>
            say("Received RemoteStopTransaction request; stopping transaction")
            ()
          case Failure(ExpectationFailed(_)) =>
            say(s"Received RemoteStopTransaction request for other transaction with ID. I'll keep waiting for a stop for $transId.")
            waitInfiniteForValidRemoteStop()
        }

      waitForValidRemoteStop()
      Thread.sleep(1000) // to simulate real chargepoint behaviour
      // handle UnlockConnectorReq if present
      //Try(expectIncoming(unlockConnectorReq.respondingWith(UnlockConnectorRes(UnlockStatus.NotSupported))))

      statusNotification(status = ChargePointStatus.Occupied(Some(OccupancyKind.Finishing)))
      Try(
        stopTransaction(transactionId = transId, idTag = Some(chargeTokenId), meterStop = valueMeterStop)
      ) match {
        case Success(_) =>
          say("Received RemoteStopTransaction request; stopped transaction")
          ()
        case Failure(ExpectationFailed(_)) =>
          say(s"RemoteStopTransaction Error")
          ()
      }

      sleep(10.seconds)

      statusNotification(status = ChargePointStatus.Available())

      say("Transaction stopped")

      sleep(5.seconds)

    } else {
      say("Authorization denied by Central System")
      fail("Not authorized")
    }
}

// vim: set ts=4 sw=4 et:
