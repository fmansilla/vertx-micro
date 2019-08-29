package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.dynamodb.dsl.AttributeType
import ar.ferman.dynamodb.dsl.ItemMapper
import ar.ferman.dynamodb.dsl.TableDefinition
import ar.ferman.dynamodb.dsl.TableKeyAttribute
import ar.ferman.dynamodb.dsl.async.Table
import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class DynamoDbUserRankingRepositoryUsingDsl(dynamoDbClient: DynamoDbAsyncClient) : UserRankingRepository {
    private val table = Table(
        dynamoDbClient,
        TableDefinition(
            name = UserRankingTable.TableName,
            hashKey = TableKeyAttribute(UserRankingTable.UserId, AttributeType.STRING)
        )
    )

    private val itemMapper = UserRankingItemMapper()

//    override suspend fun find(userId: String): UserRanking? = table.queryPaginated<UserRanking> {
//        attributes(UserRankingTable.UserId, UserRankingTable.Score)
//        mappingItems(itemMapper::fromItem)
//        where {
//            UserRankingTable.UserId eq userId
//        }
//    }.awaitAll().firstOrNull()


    //Consuming HOT STREAM
//    @ExperimentalCoroutinesApi
//    override suspend fun find(userId: String): UserRanking? = table.query<UserRanking> {
//        attributes(UserRankingTable.UserId, UserRankingTable.Score)
//        mappingItems(itemMapper::fromItem)
//        where {
//            UserRankingTable.UserId eq userId
//        }
//    }.receiveOrNull()

    //Consuming COLD STREAM
    override suspend fun find(userId: String): UserRanking? = table.query<UserRanking> {
        attributes(UserRankingTable.UserId, UserRankingTable.Score)
        mappingItems(itemMapper::fromItem)
        where {
            UserRankingTable.UserId eq userId
        }
    }.singleOrNull()

//    override suspend fun findAll(): List<UserRanking> {
//        val paginatedResult = table.scanPaginated<UserRanking> {
//            attributes(UserRankingTable.UserId, UserRankingTable.Score)
//            mappingItems(itemMapper::fromItem)
//        }
//
//        return paginatedResult.awaitAll()
//    }

    //Consuming HOT/COLD STREAM
//    @ExperimentalCoroutinesApi //Required Only for hot
//    override suspend fun findAll(): List<UserRanking> = table.scan<UserRanking> {
//        attributes(UserRankingTable.UserId, UserRankingTable.Score)
//        mappingItems(itemMapper::fromItem)
//    }.toList()

    override suspend fun put(userRanking: UserRanking) = table.put(userRanking, itemMapper::toItem)

    class UserRankingItemMapper : ItemMapper<UserRanking> {
        override fun toItem(value: UserRanking): Map<String, AttributeValue> {
            return mapOf(
                "user_id" to AttributeValue.builder().s(value.userId).build(),
                "score" to AttributeValue.builder().n("${value.score}").build()
            )
        }

        override fun fromItem(item: Map<String, AttributeValue>): UserRanking {
            return UserRanking(item["user_id"]!!.s(), item["score"]!!.n().toInt())
        }

    }
}

