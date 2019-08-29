package ar.ferman.vertxmicro.ranking.infrastructure

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*


fun TopRankingTable.create(dynamoDbClient: DynamoDbClient) {
    dynamoDbClient.createTable { tableBuilder ->
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

fun TopRankingTable.deleteIfExists(dynamoDbClient: DynamoDbClient) {
    try {
        dynamoDbClient.deleteTable { it.tableName(TopRankingTable.NAME) }
    } catch (e: ResourceNotFoundException) {
        //Ignoring non existent table
    }
}