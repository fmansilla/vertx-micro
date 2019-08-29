package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.vertxmicro.ranking.domain.TopRankingRepository
import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.infrastructure.TopRankingTable.HASHKEY
import ar.ferman.vertxmicro.ranking.infrastructure.TopRankingTable.TOP_USER_RANKING_ATTRIBUTE
import ar.ferman.vertxmicro.ranking.infrastructure.TopRankingTable.UNIQUE_KEY
import ar.ferman.vertxmicro.utils.logger
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue


class DynamoDbTopRankingRepository(private val client: DynamoDbClient) :
    TopRankingRepository {

    private val logger = logger()
    private val mapper = jacksonObjectMapper()

    override suspend fun get(): List<UserRanking> = withContext(Dispatchers.IO) {
        val json = getTopUserRankingsJson()
        parseTopUserRankings(json)
    }

    override suspend fun put(topUserRanking: UserRanking) = withContext<Unit>(Dispatchers.IO) {
        val topUserRankings = mapper.writeValueAsString(listOf(topUserRanking))

        client.putItem { putItemBuilder ->
            with(putItemBuilder) {
                tableName(TopRankingTable.NAME)
                item(
                    mapOf(
                        HASHKEY to UNIQUE_KEY.toAttributeValue(),
                        TOP_USER_RANKING_ATTRIBUTE to topUserRankings.toAttributeValue()
                    )
                )
            }
        }
    }

    override suspend fun isNewTopHighScore(topUserRanking: UserRanking): Boolean = withContext(Dispatchers.IO) {
        get().any { it.score < topUserRanking.score }
    }

    private fun getTopUserRankingsJson(): String? {
        val item = client.getItem { getItemBuilder ->
            with(getItemBuilder) {
                tableName(TopRankingTable.NAME)
                key(
                    mapOf(
                        HASHKEY to AttributeValue.builder().s(UNIQUE_KEY).build()
                    )
                )
            }
        }.item()

        return item[TOP_USER_RANKING_ATTRIBUTE]?.s()
    }

    private fun parseTopUserRankings(attributeValue: String?): List<UserRanking> {
        logger.info("Parsing top rankings json: $attributeValue")
        return attributeValue?.let { mapper.readValue<List<UserRanking>>(attributeValue) } ?: emptyList()
    }

    private fun String.toAttributeValue() = AttributeValue.builder().s(this).build()
}

