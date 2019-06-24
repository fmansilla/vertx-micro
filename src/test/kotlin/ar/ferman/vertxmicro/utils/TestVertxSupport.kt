package ar.ferman.vertxmicro.utils

import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.ext.web.client.sendBufferAwait
import kotlinx.coroutines.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

open class TestVertxSupport(private val verticleSupplier: () -> Verticle) {
    private var vertx: Vertx? = null

    @BeforeEach
    fun initVertx() {
        runBlocking {
            vertx = Vertx.vertx()
            vertx!!.deployVerticleAwait(verticleSupplier.invoke())
        }
    }

    @AfterEach
    fun stopVertx() {
        runBlocking {
            vertx?.close()
            vertx = null
        }
    }

    fun createHttpClient(host: String = "localhost", port: Int = 8080): HttpClient {
        return HttpClient(vertx!!, host, port)
    }

    class HttpClient(private val vertx: Vertx, private val host: String, private val port: Int) {

        suspend fun get(path: String): HttpResponse<Buffer> = suspendCoroutine { continuation ->
            useClient(vertx) {
                get(path).send { ar ->
                    if (ar.succeeded()) {
                        continuation.resume(ar.result())
                    } else {
                        continuation.resumeWithException(Assertions.fail("HTTP Client Failure: ${ar.cause()}"))
                    }
                }
            }
        }

        suspend fun delete(path: String): HttpResponse<Buffer> = suspendCoroutine { continuation ->
            useClient(vertx) {
                delete(path).send { ar ->
                    if (ar.succeeded()) {
                        continuation.resume(ar.result())
                    } else {
                        continuation.resumeWithException(Assertions.fail("HTTP Client Failure: ${ar.cause()}"))
                    }
                }
            }
        }

        suspend fun post(path: String, data: String): HttpResponse<Buffer> = suspendCoroutine { continuation ->
            val client = WebClient.create(vertx, WebClientOptions().apply {
                defaultPort = port
                defaultHost = host
            })


             try {
                    client.post(path).sendBuffer(Buffer.buffer(data)) {ar ->
                        client.close()
                        if (ar.succeeded()) {
                        continuation.resume(ar.result())
                    } else {
                        continuation.resumeWithException(Assertions.fail("HTTP Client Failure: ${ar.cause()}"))

                    }
                    }
            }catch (e: Throwable){
                throw e
            }finally {
//                client.close()
            }


//                post(path).sendBuffer(Buffer.buffer(data)) { ar ->
//                    if (ar.succeeded()) {
//                        continuation.resume(ar.result())
//                    } else {
//                        continuation.resumeWithException(Assertions.fail("HTTP Client Failure: ${ar.cause()}"))
//                    }
//                }
//            }
        }

        suspend fun put(path: String, data: String): HttpResponse<Buffer> = suspendCoroutine { continuation ->
            useClient(vertx) {
                put(path).sendBuffer(Buffer.buffer(data)) { ar ->
                    if (ar.succeeded()) {
                        continuation.resume(ar.result())
                    } else {
                        continuation.resumeWithException(Assertions.fail("HTTP Client Failure: ${ar.cause()}"))
                    }
                }
            }
        }

        private fun useClient(vertx: Vertx, block: WebClient.() -> Unit) {
            val client =
                WebClient.create(vertx, WebClientOptions().apply {
                    defaultPort = port
                    defaultHost = host
                })

            block(client)
            client.close()
        }
    }
}