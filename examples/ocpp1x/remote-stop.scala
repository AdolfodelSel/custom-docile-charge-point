import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for stop message")
expectIncoming(requestMatching({case r: RemoteStopTransactionReq => true}).respondingWith(RemoteStopTransactionRes(_)))
say("Received RemoteStopTransaction request; stopping transaction")

sleep(5.seconds)