import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for cancel reservation message")
expectIncoming(cancelReservationReq.respondingWith(CancelReservationRes(true)))

sleep(5.seconds)