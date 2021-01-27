import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for reserve message")
expectIncoming(reserveNowReq.respondingWith(ReserveNowRes(Reservation.Accepted)))

sleep(5.seconds)