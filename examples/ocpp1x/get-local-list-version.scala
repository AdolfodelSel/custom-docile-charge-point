import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for get local list version message")
expectIncoming(getLocalListVersionReq.respondingWith(GetLocalListVersionRes(AuthListVersion(1))))

sleep(5.seconds)