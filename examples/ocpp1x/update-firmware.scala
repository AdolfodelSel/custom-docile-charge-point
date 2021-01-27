import com.thenewmotion.ocpp.messages._

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

say("Waiting for update firmware message")
expectIncoming(updateFirmwareReq.respondingWith(UpdateFirmwareRes))

say("Received update firmware, updating...")
firmwareStatusNotification(status = FirmwareStatus.Downloading)
firmwareStatusNotification(status = FirmwareStatus.Downloaded)
firmwareStatusNotification(status = FirmwareStatus.Installed)

sleep(5.seconds)