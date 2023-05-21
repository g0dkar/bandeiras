package bandeiras.server

import bandeiras.server.extensions.log
import bandeiras.server.plugins.configureHTTP
import bandeiras.server.plugins.configureMonitoring
import bandeiras.server.plugins.configureRouting
import bandeiras.server.plugins.configureSecurity
import bandeiras.server.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

//fun main() {
//    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
//}

fun Application.module() {
    val applicationConfig = environment.config

    log.info("applicationConfig={}", applicationConfig.toMap())

    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
