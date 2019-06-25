package ar.ferman.vertxmicro

import ar.ferman.vertxmicro.ranking.CoroutineRankingHandlers
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle

class AppVerticle : CoroutineVerticle() {

    override suspend fun start() {
        // Build Vert.x Web router
        val router = createRouter()

        // Start the server
        vertx.createHttpServer()
            .requestHandler(router)
            .listenAwait(config.getInteger("http.port", 8080))
    }

    private fun createRouter() = Router.router(vertx).apply {

        route().handler(BodyHandler.create())

        val coroutineScope = this@AppVerticle

        CoroutineRankingHandlers.registerOn(this, coroutineScope)
    }
}
