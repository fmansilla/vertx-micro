//package ar.ferman.vertxmicro.ranking.rest
//
//import ar.ferman.vertxmicro.CoroutineHttpVerticle
//import ar.ferman.vertxmicro.utils.HttpTestUtils
//import com.fasterxml.jackson.core.type.TypeReference
//import com.fasterxml.jackson.databind.ObjectMapper
//import io.vertx.core.Vertx
//import io.vertx.core.buffer.Buffer
//import io.vertx.core.json.Json
//import io.vertx.ext.web.client.HttpResponse
//import io.vertx.junit5.VertxExtension
//import io.vertx.junit5.VertxTestContext
//import io.vertx.kotlin.core.closeAwait
//import io.vertx.kotlin.core.deployVerticleAwait
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.runBlocking
//import org.assertj.core.api.Assertions.assertThat
//import org.assertj.core.api.BDDAssertions.then
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//
//
//@ExtendWith(VertxExtension::class)
//class RankingResourcesTest2 {
//
//    @Test
//    fun `find all user rankings`(vertx: Vertx, testContext: VertxTestContext) = coroutineTest(vertx, testContext) {
//        with(HttpTestUtils(vertx)) {
//            val userId = "ferman"
//            val httpClient = createHttpClient()
//
//            val httpResponse = httpClient.get("/rankings")
//
//            then(readJsonBody<List<UserRankingJson>>(httpResponse)).containsExactly(
//                UserRankingJson(
//                    userId,
//                    5
//                )
//            )
//        }
//    }
//
//    @Test
//    fun `find user ranking`(vertx: Vertx, testContext: VertxTestContext) = coroutineTest(vertx, testContext) {
//        with(HttpTestUtils(vertx)) {
//            val httpClient = createHttpClient()
//            val userId = "ferman"
//
//            val httpResponse = httpClient.get("/rankings/$userId")
//
//            then(httpResponse.bodyAsJson(UserRankingJson::class.java)).satisfies {
//                it isEqualTo UserRankingJson(userId, 5)
//            }
//        }
//    }
//
//    @Test
//    fun `post user ranking`(vertx: Vertx, testContext: VertxTestContext) = coroutineTest(vertx, testContext) {
//        with(HttpTestUtils(vertx)) {
//            val httpClient = createHttpClient()
//            val userId = "ferman"
//
//            val httpResponse = httpClient.post(
//                "/rankings", Json.encode(
//                    UserRankingJson(
//                        userId,
//                        5
//                    )
//                )
//            )
//
//            then(httpResponse.statusCode()).isEqualTo(200)
//        }
//    }
//
//    private inline fun <reified T> readJsonBody(httpResponse: HttpResponse<Buffer>): T {
//        return ObjectMapper().readValue(httpResponse.bodyAsString(), object : TypeReference<T>() {})
//    }
//
//    private fun coroutineTest(vertx: Vertx, testContext: VertxTestContext, block: suspend CoroutineScope.() -> Unit) =
//        runBlocking {
//            vertx.deployVerticleAwait(CoroutineHttpVerticle())
//            block()
//            testContext.completeNow()
//            vertx.closeAwait()
//        }
//
//    private infix fun Any?.isEqualTo(expected: Any?) {
//        assertThat(this).isEqualTo(expected)
//    }
//}