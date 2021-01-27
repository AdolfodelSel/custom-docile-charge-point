import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for send local list message")
expectIncoming(sendLocalListReq.respondingWith(SendLocalListRes(UpdateStatusWithoutHash.Failed)))

sleep(5.seconds)