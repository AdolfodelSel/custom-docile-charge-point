import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for clear charging profile message")
expectIncoming(clearChargingProfileReq.respondingWith(ClearChargingProfileRes(ClearChargingProfileStatus.Accepted)))

sleep(5.seconds)