package ar.ferman.vertxmicro.ranking.infrastructure

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.domain.UserRankingRepository
import ar.ferman.vertxmicro.ranking.infrastructure.UserRankingTable.UserId
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object UserRankingTable {
    val TableName = "user_rankings"

    val UserId = "user_id"
    val Score = "score"
}

class DynamoDbUserRankingRepository(private val dynamoDbClient: DynamoDbAsyncClient) : UserRankingRepository {
    override suspend fun find(userId: String): UserRanking? = suspendCoroutine { continuation ->
        dynamoDbClient.query {
            it.tableName(UserRankingTable.TableName)
                .keyConditionExpression("$UserId = :userId")
                .expressionAttributeValues(mapOf(":userId" to AttributeValue.builder().s(userId).build()))
        }.whenComplete { result, error ->
            if (error != null) {
                continuation.resumeWithException(error)
            } else {
                continuation.resume(result?.items()?.firstOrNull()?.toUserRanking())
            }
        }
    }

    override suspend fun findAll(): List<UserRanking> = suspendCoroutine { continuation ->
        dynamoDbClient.scan {
            it.tableName(UserRankingTable.TableName)
                .limit(10) }
            .whenComplete { result, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else {
                    continuation.resume(result?.items()?.map { it.toUserRanking() } ?: emptyList())
                }
            }
    }

    override suspend fun put(userRanking: UserRanking) = suspendCoroutine<Unit> { continuation ->
        dynamoDbClient.putItem {
            it.tableName(UserRankingTable.TableName).item(userRanking.toItem())
        }.whenComplete { _, error ->
            if (error != null) {
                continuation.resumeWithException(error)
            } else {
                continuation.resume(Unit)
            }
        }
    }

    private fun Map<String, AttributeValue>.toUserRanking(): UserRanking {
        return UserRanking(get("user_id")!!.s(), get("score")!!.n().toInt())
    }

    private fun UserRanking.toItem(): Map<String, AttributeValue> {
        return mapOf(
            "user_id" to AttributeValue.builder().s(userId).build(),
            "score" to AttributeValue.builder().n("$score").build()
        )
    }
}