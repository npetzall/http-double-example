package example

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GetQuotationTest extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:3000")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("GetQuotation")
    .exec(http("example")
      .post("/example")
      .body(RawFileBody("example/getQuotationRequest.xml")).asXML
      .check(status.is(200))
    )
  setUp(
    scn.inject(constantUsersPerSec(40) during(240 seconds) randomized)
  ).protocols(httpConf)
}