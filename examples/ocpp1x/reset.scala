import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for reset message")
expectIncoming(resetReq.respondingWith(ResetRes(accepted = true)))

sleep(5.seconds)