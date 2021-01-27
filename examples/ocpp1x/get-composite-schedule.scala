import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for get composite schedule message")
expectIncoming(getCompositeScheduleReq.respondingWith(GetCompositeScheduleRes(CompositeScheduleStatus.Rejected)))

sleep(5.seconds)