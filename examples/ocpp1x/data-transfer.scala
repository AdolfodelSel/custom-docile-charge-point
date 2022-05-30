import java.time.ZonedDateTime
import chargepoint.docile.dsl._
import com.thenewmotion.ocpp.messages.v1x._
import scala.concurrent.duration._
import scala.util._
import scala.io.StdIn.{readLine,readInt}

say("Simulate active connection")
statusNotification(scope = ConnectorScope(-1))

while(true){
    say("Waiting data transfer")
    val noTimeout: AwaitTimeout = InfiniteAwaitTimeout
    expectIncoming(
        requestMatching({case r: ChargePointDataTransferReq => r})
        .respondingWith(ChargePointDataTransferRes(DataTransferStatus.Accepted))
    )(noTimeout)
    sleep(5.seconds)
}