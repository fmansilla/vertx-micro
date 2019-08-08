package ar.ferman.vertxmicro.ranking.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import redis.clients.jedis.Jedis

@Testcontainers
class RedisBackedCacheTest {

    companion object {
        @Container
        @JvmField
        val redisContainer: KGenericContainer = KGenericContainer("redis:3.0.6").withExposedPorts(6379)
    }

    private lateinit var jedis: Jedis

    @BeforeEach
    fun setUp() {
        jedis = Jedis(redisContainer.containerIpAddress, redisContainer.getMappedPort(6379)!!)
    }


    @Test
    internal fun `test redis`() {

        assertThat(jedis.ping("HELLO")).isEqualTo("HELLO")
    }
}