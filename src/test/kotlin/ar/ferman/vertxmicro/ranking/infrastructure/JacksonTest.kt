package ar.ferman.vertxmicro.ranking.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JacksonTest {
    data class Movie(
        var name: String,
        var studio: String,
        var rating: Float? = 1f
    )

    private val mapper = jacksonObjectMapper()

    @Test
    fun whenSerializeMovie_thenSuccess() {
        val movie = Movie("Endgame", "Marvel", 9.2f)
        val serialized = mapper.writeValueAsString(movie)

        val json = """{"name":"Endgame","studio":"Marvel","rating":9.2}"""
        assertThat(serialized).isEqualTo(json)
    }

    @Test
    fun whenDeserializeMovie_thenSuccess() {
        val json = """{"name":"Endgame","studio":"Marvel","rating":9.2}"""
        val movie: Movie = mapper.readValue(json)

        with(movie) {
            assertThat(name).isEqualTo("Endgame")
            assertThat(studio).isEqualTo("Marvel")
            assertThat(rating).isEqualTo(9.2f)
        }
    }
}