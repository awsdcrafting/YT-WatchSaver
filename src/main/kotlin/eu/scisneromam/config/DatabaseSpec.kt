package eu.scisneromam.config

import com.uchuhimo.konf.ConfigSpec

object DatabaseSpec : ConfigSpec()
{
    val tableName by optional("YTWatchSaver", description = "The table name in the db")
    val filePath by optional("./ytws.db", description = "The path to the db file")
    val fileSave by optional("./ytws", description = "Where the file should be saved to in case of a call to /save")

    object UserSpec : ConfigSpec()
    {
        val name by optional("", description = "Name for the db user")
        val password by optional("", description = "Password for the db user")
    }

}