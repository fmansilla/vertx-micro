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
import software.amazon.awssdk.services.dynamodb.model.*

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
        deleteTableIfExists()
        createTable()
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

        repository.put(UserRanking("ferman", 10))

        then(repository.get()).containsExactly(UserRanking("ferman", 10))
    }

    @Test
    fun `check if new top high score should be in top user rankings`() = runBlocking<Unit> {
        val repository = DynamoDbTopRankingRepository(client)
        repository.put(UserRanking("ferman", 10))

        val result = repository.isNewTopHighScore(UserRanking("ferman", 15))

        then(result).isEqualTo(true)
    }

    @Test
    fun `check if not new top high score should be in top user rankings`() = runBlocking<Unit> {
        val repository = DynamoDbTopRankingRepository(client)
        repository.put(UserRanking("ferman", 10))

        val result = repository.isNewTopHighScore(UserRanking("ferman", 5))

        then(result).isEqualTo(false)
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

    private fun createTable() {
        client.createTable { tableBuilder ->
            with(tableBuilder) {
                tableName(TopRankingTable.NAME)
                keySchema(
                    KeySchemaElement.builder()
                        .attributeName(TopRankingTable.HASHKEY).keyType(KeyType.HASH)
                        .build()
                )
                attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName(TopRankingTable.HASHKEY).attributeType(ScalarAttributeType.S)
                        .build()
                )
                billingMode(BillingMode.PAY_PER_REQUEST)
            }
        }
    }

    private fun deleteTableIfExists() {
        try {
            client.deleteTable { it.tableName(TopRankingTable.NAME) }
        } catch (e: ResourceNotFoundException) {
            //Ignoring non existent table
        }
    }

    private fun String.toAttributeValue() = AttributeValue.builder().s(this).build()
}
