package eu.scisneromam.config

import com.uchuhimo.konf.ConfigSpec

/**
 * WebServer configuration
 */
object ServerSpec : ConfigSpec()
{
    //Port the webserver starts on
    val port by optional(15643, "port", "Port of server, default: 15643")

    //A call to this url stops the application
    val shutDownUrl by optional("/stop", description = "When a call to this url is received stop the server")
}