package ar.ferman.vertxmicro.ranking.infrastructure

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
            KGenericContainer("amazon/dynamodb-local:1.11.477").withExposedPorts(DYNAMO_PORT)
    }

    @Test
    fun someTestMethod() = runBlocking {
        val dynamoDbClient = DynamoDbAsyncClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI("http://localhost:$DYNAMO_PORT"))
            .credentialsProvider { AwsBasicCredentials.create("access", "secret") }.build()

        Unit
    }
}