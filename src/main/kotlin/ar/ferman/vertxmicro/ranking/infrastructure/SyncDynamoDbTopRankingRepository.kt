package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.vertxmicro.ranking.domain.SyncTopRankingRepository
import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.infrastructure.TopRankingTable.HASHKEY
import ar.ferman.vertxmicro.ranking.infrastructure.TopRankingTable.TOP_USER_RANKING_ATTRIBUTE
import ar.ferman.vertxmicro.ranking.infrastructure.TopRankingTable.UNIQUE_KEY
import ar.ferman.vertxmicro.utils.logger
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue


class SyncDynamoDbTopRankingRepository(private val client: DynamoDbClient) :
    SyncTopRankingRepository {

    private val logger = logger()
    private val mapper = jacksonObjectMapper()

    override fun get(): List<UserRanking> {
        val json = getTopUserRankingsJson()

        return parseTopUserRankings(json)
    }

    override fun put(topUserRankings: List<UserRanking>) {
        val serializedTopUserRankings = mapper.writeValueAsString(topUserRankings)

        client.putItem { putItemBuilder ->
            with(putItemBuilder) {
                tableName(TopRankingTable.NAME)
                item(
                    mapOf(
                        HASHKEY to UNIQUE_KEY.toAttributeValue(),
                        TOP_USER_RANKING_ATTRIBUTE to serializedTopUserRankings.toAttributeValue()
                    )
                )
            }
        }
    }

    private fun getTopUserRankingsJson(): String? {
        val getItemResponse = client.getItem { getItemBuilder ->
            with(getItemBuilder) {
                tableName(TopRankingTable.NAME)
                key(
                    mapOf(
                        HASHKEY to AttributeValue.builder().s(UNIQUE_KEY).build()
                    )
                )
            }
        }
        val item = getItemResponse.item()

        return item[TOP_USER_RANKING_ATTRIBUTE]?.s()
    }

    private fun parseTopUserRankings(attributeValue: String?): List<UserRanking> {
        logger.info("Parsing top rankings json: $attributeValue")
        return attributeValue?.let { mapper.readValue<List<UserRanking>>(attributeValue) } ?: emptyList()
    }

    private fun String.toAttributeValue() = AttributeValue.builder().s(this).build()
}

