package eu.scisneromam.config

import com.uchuhimo.konf.ConfigSpec

object ServerConfig : ConfigSpec("server")
{
    val port by optional(15643, "port", "Port of server, default: 15643")
}