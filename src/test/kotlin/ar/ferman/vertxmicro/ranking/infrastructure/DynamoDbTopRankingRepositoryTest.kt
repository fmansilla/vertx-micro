package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Testcontainers
class DynamoDbTopRankingRepositoryTest {
    companion object {
        @Container
        @JvmField
        val dynamoDbContainer: KGenericContainer = DynamoDbForTests.createContainer()
    }

    private lateinit var client: DynamoDbClient
    private val mapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        client = DynamoDbForTests.createSyncClient(dynamoDbContainer)
        TopRankingTable.deleteIfExists(client)
        TopRankingTable.create(client)
    }

    @Test
    fun `find top rankings when empty`() = runBlocking<Unit> {
        val repository = DynamoDbTopRankingRepository(client)

        val topUserRankings = repository.get()

        then(topUserRankings).isEmpty()
    }

    @Test
    fun `find top rankings when not empty`() = runBlocking<Unit> {
        givenExistingTopRanking()
        val repository = DynamoDbTopRankingRepository(client)

        val topUserRankings = repository.get()

        then(topUserRankings).isNotEmpty
    }

    @Test
    fun `put ranking into top rankings`() = runBlocking<Unit> {
        val repository = DynamoDbTopRankingRepository(client)

        repository.put(listOf(UserRanking("ferman", 10)))

        then(repository.get()).containsExactly(UserRanking("ferman", 10))
    }

    private fun givenExistingTopRanking() {
        client.putItem { putItemBuilder ->
            with(putItemBuilder) {
                tableName(TopRankingTable.NAME)
                item(
                    mapOf(
                        TopRankingTable.HASHKEY to TopRankingTable.UNIQUE_KEY.toAttributeValue(),
                        TopRankingTable.TOP_USER_RANKING_ATTRIBUTE to sampleTopRanking().toAttributeValue()
                    )
                )
            }
        }
    }

    private fun sampleTopRanking(): String {
        return mapper.writeValueAsString(listOf(UserRankingJson("someone", 1)))
    }

    private fun String.toAttributeValue() = AttributeValue.builder().s(this).build()
}
