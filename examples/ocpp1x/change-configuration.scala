import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for change configuration message")
expectIncoming(changeConfigurationReq.respondingWith(ChangeConfigurationRes(ConfigurationStatus.Accepted)))

sleep(5.seconds)