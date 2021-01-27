import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for cancel reservation message")
expectIncoming(changeAvailabilityReq.respondingWith(ChangeAvailabilityRes(AvailabilityStatus.Accepted)))

sleep(5.seconds)