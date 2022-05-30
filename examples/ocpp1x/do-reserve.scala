import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))


say("Waiting for reserve message")

def waitForReserveMessage(): Unit = {
    val result: Seq[ChargePointReq] =
        expectAllIgnoringUnmatched(
            reserveNowReq.respondingWith(ReserveNowRes(Reservation.Accepted)),
            cancelReservationReq.respondingWith(CancelReservationRes(true))
        )
    Console.println(result)
    waitForReserveMessage()
}

waitForReserveMessage()

say("Me voy--------------------------")
sleep(5.seconds)