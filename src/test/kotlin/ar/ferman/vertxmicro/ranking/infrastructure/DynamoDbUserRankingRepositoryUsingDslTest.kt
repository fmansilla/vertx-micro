package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Testcontainers
class DynamoDbUserRankingRepositoryUsingDslTest {

    companion object {
        @Container
        @JvmField
        val dynamoDbContainer: KGenericContainer = DynamoDbForTests.createContainer()
    }

    @Test
    fun someTestMethod() = runBlocking {

        val dynamoDbClient = DynamoDbForTests.createAsyncClient(dynamoDbContainer)

        dynamoDbClient.deleteUserRankingTable()
        dynamoDbClient.createUserRankingTable()

        val repository = DynamoDbUserRankingRepositoryUsingDsl(dynamoDbClient)

        assertThat(repository.findAll()).isEmpty()

        assertThat(repository.find("a")).isNull()

        repository.put(UserRanking("a", 5))

        assertThat(repository.find("a")).isEqualTo(UserRanking("a", 5))

        assertThat(repository.findAll()).containsExactly(UserRanking("a", 5))

        Unit
    }

    private suspend fun DynamoDbAsyncClient.deleteUserRankingTable() {
        suspendCoroutine<Unit> { continuation ->
            deleteTable {
                it.tableName(UserRankingTable.TableName)
            }.whenComplete { _, _ -> continuation.resume(Unit) }
        }
    }

    private suspend fun DynamoDbAsyncClient.createUserRankingTable() {
        suspendCoroutine<Unit> { continuation ->
            createTable {
                it.tableName(UserRankingTable.TableName)
                it.keySchema(
                    KeySchemaElement.builder()
                        .attributeName(UserRankingTable.UserId).keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName(UserRankingTable.Score).keyType(KeyType.RANGE)
                        .build()

                )
                it.attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName(UserRankingTable.UserId).attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName(UserRankingTable.Score).attributeType(ScalarAttributeType.N)
                        .build()
                )
                it.provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(1).writeCapacityUnits(1).build())
            }.whenComplete { _, _ -> continuation.resume(Unit) }
        }
    }
}