package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

@Testcontainers
class SyncDynamoDbTopRankingRepositoryTest {
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
    fun `find top rankings when empty`() {
        val repository = SyncDynamoDbTopRankingRepository(client)

        val topUserRankings = repository.get()

        then(topUserRankings).isEmpty()
    }

    @Test
    fun `find top rankings when not empty`() {
        givenExistingTopRanking()
        val repository = SyncDynamoDbTopRankingRepository(client)

        val topUserRankings = repository.get()

        then(topUserRankings).isNotEmpty
    }

    @Test
    fun `put ranking into top rankings`() {
        val repository = SyncDynamoDbTopRankingRepository(client)

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
