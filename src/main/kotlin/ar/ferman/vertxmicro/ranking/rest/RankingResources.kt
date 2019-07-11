package ar.ferman.vertxmicro.ranking.rest

import io.vertx.ext.web.Router
import kotlinx.coroutines.CoroutineScope

object RankingResources {


    fun registerOn(router: Router, coroutineScope: CoroutineScope) = router.run {
        get("/rankings").coroutineHandler(coroutineScope) {
            it.response().endWithJson(
                listOf(
                    mapOf(
                        "userId" to "ferman",
                        "score" to 5
                    )
                )
            )
        }
    }
}
