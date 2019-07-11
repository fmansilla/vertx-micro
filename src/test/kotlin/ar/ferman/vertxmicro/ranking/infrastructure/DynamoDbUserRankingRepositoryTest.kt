package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//Workaround for Kotlin type inference issue
class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

@Testcontainers
class DynamoDbUserRankingRepositoryTest {

    companion object {
        private const val DYNAMO_PORT = 8000

        @Container
        @JvmField
        val dynamoDb: KGenericContainer =
            KGenericContainer("amazon/dynamodb-local:1.11.119").withExposedPorts(DYNAMO_PORT)
    }

    @Test
    fun someTestMethod() = runBlocking {
        val dynamoDbClient = DynamoDbAsyncClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI("http://localhost:$DYNAMO_PORT"))
            .credentialsProvider { AwsBasicCredentials.create("access", "secret") }.build()

        dynamoDbClient.deleteUserRankingTable()
        dynamoDbClient.createUserRankingTable()

        val repository = DynamoDbUserRankingRepository(dynamoDbClient)

        assertThat(repository.findAll()).isEmpty()

        assertThat(repository.find("a")).isNull()

        repository.put(UserRanking("a", 5))

        assertThat(repository.find("a")).isEqualTo(UserRanking("a", 5))

        Unit
    }

    suspend fun DynamoDbAsyncClient.deleteUserRankingTable() {
        suspendCoroutine<Unit> { continuation ->
            deleteTable {
                it.tableName(UserRankingTable.TableName)
            }.whenComplete { _, _ -> continuation.resume(Unit) }
        }
    }

    suspend fun DynamoDbAsyncClient.createUserRankingTable() {
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