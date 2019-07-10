package ar.ferman.vertxmicro.ranking.rest

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json


/**
 * Extension to the HTTP response to output JSON objects.
 */
fun HttpServerResponse.endWithJson(obj: Any) {
    this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
}