package ar.ferman.vertxmicro.ranking.infrastructure

import org.testcontainers.containers.GenericContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

//Workaround for Kotlin type inference issue
class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

object DynamoDbForTests {
    const val DYNAMO_PORT = 8000

    fun createAsyncClient(container: KGenericContainer) = DynamoDbAsyncClient.builder()
        .region(Region.US_EAST_1)
        .endpointOverride(URI("http://${container.containerIpAddress}:${container.getMappedPort(DYNAMO_PORT)}"))
        .credentialsProvider { AwsBasicCredentials.create("access", "secret") }.build()

    fun createSyncClient(container: KGenericContainer) = DynamoDbClient.builder()
        .region(Region.US_EAST_1)
        .endpointOverride(URI("http://${container.containerIpAddress}:${container.getMappedPort(DYNAMO_PORT)}"))
        .credentialsProvider { AwsBasicCredentials.create("access", "secret") }.build()

    fun createContainer(): KGenericContainer {
        return KGenericContainer("amazon/dynamodb-local:1.11.477")
            .withExposedPorts(DYNAMO_PORT)
    }
}
