package ar.ferman.vertxmicro.ranking.restold

import ar.ferman.vertxmicro.ranking.domain.UserRanking
import ar.ferman.vertxmicro.ranking.rest.UserRankingJson
import ar.ferman.vertxmicro.ranking.rest.endWithJson
import ar.ferman.vertxmicro.ranking.rest.toJsonRepresentation
import io.vertx.core.Handler
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

object RankingHandlers {


    fun registerOn(router: Router) {
        router.apply {
            get("/rankings").handler(handlerFindAllRankings)
            get("/rankings/:id").handler(handlerFindRanking)
            post("/rankings/:id").handler(handlerPublishRanking)
        }
    }

    // Handlers
    private val handlerFindRanking = Handler<RoutingContext> { req ->
        val userRanking = UserRanking("ferman", 5)

        req.response().endWithJson(userRanking.toJsonRepresentation())
    }

    private val handlerFindAllRankings = Handler<RoutingContext> { req ->
        val userRankings = listOf(UserRanking("ferman", 5))

        req.response().endWithJson(userRankings.map { it.toJsonRepresentation() })
    }

    private val handlerPublishRanking = Handler<RoutingContext> { req ->
        val userRanking = Json.decodeValue(req.body, UserRankingJson::class.java).toUserRanking()
        println("Published: $userRanking")

        req.response().setStatusCode(200).end()
    }
}

