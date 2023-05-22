package bandeiras.server.plugins

import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.protobuf.protobuf
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                namingStrategy = JsonNamingStrategy.SnakeCase
            },
        )
        protobuf()
    }

    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
