import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.hocon
import com.uchuhimo.konf.source.hocon.toHocon
import eu.scisneromam.config.ServerConfig
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.html.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

fun HTML.index()
{
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
    }
}

object Statics
{
    lateinit var mainLogger: Logger

    lateinit var config: Config
}

fun main()
{

    Statics.mainLogger = LoggerFactory.getLogger("YT-WatchSaver-Server")
    val logger = Statics.mainLogger
    logger.info("Startup")
    logger.info("Loading config")
    val configFile = File("config.conf")
    //load config
    Statics.config = Config { addSpec(ServerConfig) }


    if (!configFile.exists())
    {
        Statics.config.toHocon.toFile(configFile)
        logger.info("No config file existed, created one, stopping")
        return
    } else
    {
        Statics.config = Statics.config.from.hocon.file(configFile)
    }

    logger.info("Loaded config")

    val port = Statics.config[ServerConfig.port]
    logger.info("Starting webserver on port $port")
    val server = embeddedServer(Netty, port = port, host = "127.0.0.1") {
        module()
    }.start(wait = true)
    logger.info("Webserver started on ${server.application}")
}

fun Application.module()
{
    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
        get("/hello") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
    }
}