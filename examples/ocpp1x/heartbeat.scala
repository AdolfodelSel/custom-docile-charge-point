send(HeartbeatReq)
expectIncoming(matching { case HeartbeatRes(_) => })

sleep(5.seconds)