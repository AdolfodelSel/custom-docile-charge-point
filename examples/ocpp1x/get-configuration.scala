import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for get configuration message")
expectIncoming(getConfigurationReq.respondingWith(GetConfigurationRes(values = List[KeyValue](), unknownKeys = List[String]())))

sleep(5.seconds)