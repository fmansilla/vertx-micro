package ar.ferman.vertxmicro.ranking

import ar.ferman.vertxmicro.AppVerticle
import ar.ferman.vertxmicro.utils.TestVertxSupport
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.Json
import io.vertx.ext.web.client.HttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class RankingHandlersTest : TestVertxSupport({ AppVerticle() }) {

    @Test
    fun `find all user rankings`() = coroutineTest {
        val userId = "ferman"
        val httpClient = createHttpClient()

        val httpResponse = httpClient.get("/rankings")

        then(readJsonBody<List<UserRankingJson>>(httpResponse)).containsExactly(UserRankingJson(userId, 5))
    }

    @Test
    fun `find user ranking`() = coroutineTest {
        val httpClient = createHttpClient()
        val userId = "ferman"

        val httpResponse = httpClient.get("/rankings/$userId")

        then(httpResponse.bodyAsJson(UserRankingJson::class.java)).satisfies {
            it isEqualTo UserRankingJson(userId, 5)
        }
    }

    @Test
    fun `put user ranking`() = coroutineTest {
        val httpClient = createHttpClient()
        val userId = "ferman"

        val httpResponse = httpClient.post("/rankings/$userId", Json.encode(UserRankingJson(userId, 5)))

        then(httpResponse.statusCode()).isEqualTo(200)
    }

    private inline fun <reified T> readJsonBody(httpResponse: HttpResponse<Buffer>): T {
        return ObjectMapper().readValue(httpResponse.bodyAsString(), object : TypeReference<T>() {})
    }

    private fun coroutineTest(block: suspend CoroutineScope.() -> Unit) {
        runBlocking { block() }
    }

    private infix fun Any?.isEqualTo(expected: Any?) {
        assertThat(this).isEqualTo(expected)
    }
}