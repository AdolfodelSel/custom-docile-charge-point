send(AuthorizeReq(idTag = "ABCDEF01020304"))
expectIncoming(matching { case AuthorizeRes(idTi) if idTi.status == AuthorizationStatus.Accepted => })

sleep(5.seconds)
