import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for set charging profile message")
expectIncoming(setChargingProfileReq.respondingWith(SetChargingProfileRes(ChargingProfileStatus.Accepted)))

sleep(5.seconds)