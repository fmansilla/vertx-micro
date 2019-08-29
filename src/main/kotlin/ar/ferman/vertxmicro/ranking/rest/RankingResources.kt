package ar.ferman.vertxmicro.ranking.rest

import ar.ferman.vertxmicro.ranking.RankingConfiguration.Actions
import ar.ferman.vertxmicro.ranking.rest.handler.FindTopUserRankingsHandler
import ar.ferman.vertxmicro.ranking.rest.handler.FindUserRankingHandler
import ar.ferman.vertxmicro.ranking.rest.handler.PublishScoreHandler
import io.vertx.ext.web.Router
import kotlinx.coroutines.CoroutineScope

object RankingResources {

    private val findTopUserRankingsHandler =
        FindTopUserRankingsHandler(Actions.FindTopUserRankings)
    private val findUserRankingHandler = FindUserRankingHandler(Actions.FindUserRanking)
    private val publishScoreHandler = PublishScoreHandler(Actions.PublishScore)

    fun registerOn(router: Router, coroutineScope: CoroutineScope) = router.run {
        get("/top-rankings").coroutineHandler(coroutineScope, findTopUserRankingsHandler)
        get("/rankings/:id").coroutineHandler(coroutineScope, findUserRankingHandler)
        post("/rankings").coroutineHandler(coroutineScope, publishScoreHandler)
    }
}
