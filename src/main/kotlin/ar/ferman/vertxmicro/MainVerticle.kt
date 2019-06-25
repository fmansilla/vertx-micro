package ar.ferman.vertxmicro

import ar.ferman.vertxmicro.ranking.RankingHandlers
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

@Suppress("unused")
class MainVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {
        val router = createRouter()

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(config().getInteger("http.port", 8080)) { result ->
                if (result.succeeded()) {
                    startFuture.complete()
                } else {
                    startFuture.fail(result.cause())
                }
            }
    }

    private fun createRouter() = Router.router(vertx).apply {
        route().handler(BodyHandler.create())

        RankingHandlers.registerOn(this)
    }
}


