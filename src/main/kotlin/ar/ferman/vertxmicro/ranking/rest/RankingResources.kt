package ar.ferman.vertxmicro.ranking.rest

import ar.ferman.vertxmicro.ranking.RankingConfiguration
import ar.ferman.vertxmicro.ranking.rest.handler.FindAllUserRankingsHandler
import ar.ferman.vertxmicro.ranking.rest.handler.FindUserRankingHandler
import ar.ferman.vertxmicro.ranking.rest.handler.PublishScoreHandler
import io.vertx.ext.web.Router
import kotlinx.coroutines.CoroutineScope

object RankingResources {

    private val findAllUserRankingsHandler =
        FindAllUserRankingsHandler(RankingConfiguration.Actions.FindAllUserRankings)
    private val findUserRankingHandler = FindUserRankingHandler(RankingConfiguration.Actions.FindUserRanking)
    private val publishScoreHandler = PublishScoreHandler(RankingConfiguration.Actions.PublishScore)

    fun registerOn(router: Router, coroutineScope: CoroutineScope) = router.run {
        get("/rankings").coroutineHandler(coroutineScope, findAllUserRankingsHandler)
        get("/rankings/:id").coroutineHandler(coroutineScope, findUserRankingHandler)
        post("/rankings").coroutineHandler(coroutineScope, publishScoreHandler)
    }
}
