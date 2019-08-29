package ar.ferman.vertxmicro.ranking

import ar.ferman.vertxmicro.ranking.action.FindTopUserRankings
import ar.ferman.vertxmicro.ranking.action.FindUserRanking
import ar.ferman.vertxmicro.ranking.action.PublishScore
import ar.ferman.vertxmicro.ranking.domain.TopRankingRepository
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository
import ar.ferman.vertxmicro.ranking.infrastructure.DynamoDbTopRankingRepository
import ar.ferman.vertxmicro.ranking.infrastructure.InMemoryUserRankingRepository
import ar.ferman.vertxmicro.utils.Environment
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

object RankingConfiguration {

    private val dynamoDbClient: DynamoDbClient by lazy {
        DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(URI(Environment["DYNAMO_DB_HOST"] ?: "http://localhost:8000"))
            .credentialsProvider { AwsBasicCredentials.create("access", "secret") }.build()
    }
    private val userRankingRepository: UserRankingRepository by lazy { InMemoryUserRankingRepository() }

    private val topRankingRepository: TopRankingRepository by lazy { DynamoDbTopRankingRepository(dynamoDbClient) }

    object Actions {
        val FindTopUserRankings: FindTopUserRankings by lazy { FindTopUserRankings(topRankingRepository) }
        val FindUserRanking: FindUserRanking by lazy { FindUserRanking(userRankingRepository) }
        val PublishScore: PublishScore by lazy { PublishScore(userRankingRepository, topRankingRepository) }
    }
}