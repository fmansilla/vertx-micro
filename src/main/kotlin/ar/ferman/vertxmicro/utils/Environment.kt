package ar.ferman.vertxmicro.utils

import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*
import java.util.concurrent.ConcurrentHashMap


object Environment {
    private const val LOCAL_ENVIRONMENT_FILE = ".env"

    private val logger = logger()

    private val config by lazy {
        ConcurrentHashMap<String, String>().also {
            it.loadFromFile(LOCAL_ENVIRONMENT_FILE)
            it.putAll(System.getenv())
        }
    }

    operator fun get(key: String): String? {
        return config[key]
    }

    operator fun set(key: String, value: String) {
        config[key] = value
    }

    private fun MutableMap<String, String>.loadFromFile(filename: String) {
        try {

            FileReader(filename).use {
                val properties = Properties()
                properties.load(it)

                properties.forEach { key, value ->
                    this[key.toString()] = value.toString()
                }
            }
            logger.info("$filename config file was loaded")
        } catch (e: FileNotFoundException) {
            logger.info("$filename config file is not available")
        } catch (e: Exception) {
            logger.info("Unexpected error loading $filename config file", e)
        }
    }
}