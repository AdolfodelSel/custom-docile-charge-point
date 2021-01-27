import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for clear cache message")
expectIncoming(clearCacheReq.respondingWith(ClearCacheRes(true)))

sleep(5.seconds)