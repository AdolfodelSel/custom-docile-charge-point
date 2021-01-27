import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for unlock connector message")
expectIncoming(unlockConnectorReq.respondingWith(UnlockConnectorRes(UnlockStatus.NotSupported)))

sleep(5.seconds)