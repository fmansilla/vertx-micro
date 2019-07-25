package ar.ferman.vertxmicro

import ar.ferman.vertxmicro.utils.logger
import io.vertx.core.Vertx

object Application {

    private val logger = logger()

    @JvmStatic
    fun main(args: Array<String>) {
        Vertx.vertx().deployVerticle(CoroutineHttpVerticle::class.java.canonicalName) {
            if(it.succeeded()) {
                logger.info("Verticle deployed successfully")
            }
        }
    }
}
