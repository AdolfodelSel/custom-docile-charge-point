import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for get diagnostic message")
expectIncoming(getDiagnosticsReq.respondingWith(GetDiagnosticsRes(fileName = Some(""))))

say("Received get diagnostic, updating...")
diagnosticsStatusNotification(status = DiagnosticsStatus.Uploading)
diagnosticsStatusNotification(status = DiagnosticsStatus.Uploaded)

sleep(5.seconds)