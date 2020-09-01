import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.hocon
import eu.scisneromam.cgson
import eu.scisneromam.config.DatabaseSpec
import eu.scisneromam.config.ServerSpec
import eu.scisneromam.config.toCommentedHocon
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.ShutDownUrl
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
    Statics.config = Config {
        addSpec(ServerSpec)
        addSpec(DatabaseSpec)
    }

    //when no previous config exists
    if (!configFile.exists())
    {
        //save current config
        Statics.config.toCommentedHocon.toFile(configFile)
        logger.info("No config file existed, created one, stopping")
        return
    } else
    {
        //load previous config
        Statics.config = Statics.config.from.hocon.file(configFile)
    }

    logger.info("Loaded config")

    val port = Statics.config[ServerSpec.port]
    val host = "127.0.0.1"
    logger.info("Starting webserver on port http://$host:$port")
    val server = embeddedServer(Netty, port = port, host = host) {
        module()
    }.start(wait = true)
    logger.info("Webserver stopped")
}

fun Application.module()
{
    install(ShutDownUrl.ApplicationCallFeature) {
        // The URL that will be intercepted
        shutDownUrl = Statics.config[ServerSpec.shutDownUrl]
        // A function that will be executed to get the exit code of the process
        exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
    }

    install(ContentNegotiation) {
        cgson {
            //serializeNulls()
            enableComplexMapKeySerialization()
            setLenient()
            setPrettyPrinting()
        }
    }


    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
        get("/hello") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
    }
}