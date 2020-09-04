package eu.scisneromam.database

import com.uchuhimo.konf.Config
import eu.scisneromam.config.DatabaseSpec
import eu.scisneromam.models.SiteModel
import io.ktor.http.URLParserException
import io.ktor.http.Url
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection

class DBConnection(config: Config)
{
    val db = Database.connect(
        "jdbc:sqlite:${config[DatabaseSpec.filePath]}",
        "org.sqlite.JDBC",
        config[DatabaseSpec.UserSpec.name],
        config[DatabaseSpec.UserSpec.password]
    )


    init
    {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    fun transformUrl(url: String): String
    {
        return Url(url).let {
            it.host + if (it.specifiedPort != it.protocol.defaultPort)
            {
                ":${it.specifiedPort}"
            } else
            {
                ""
            }
        }
    }

    fun addSite(site: SiteModel)
    {
        val url = try
        {
            transformUrl(site.url)
        } catch (_: URLParserException)
        {
            return
        }

        transaction(db) {
            val sites = SiteEntity.find { SiteTable.siteUrl eq url }
            when (sites.count())
            {
                0L ->
                {
                    SiteEntity.new {
                        siteTitle = site.title
                        siteUrl = url
                        times = 1
                    }
                }
                1L ->
                {
                    val dbSite = sites.first()
                    dbSite.times += 1
                    dbSite
                }
                else -> throw IllegalStateException("A site url is multiple times in the db! $url")
            }
        }
    }

    fun getSite(url: String): SiteModel?
    {
        val url = try
        {
            transformUrl(url)
        } catch (_: URLParserException)
        {
            return null
        }
        return transaction(db) {
            val sites = SiteEntity.find { SiteTable.siteUrl eq url }
            when (sites.count())
            {
                0L ->
                {
                    null
                }
                1L ->
                {
                    val dbSite = sites.first()
                    dbSite
                }
                else -> throw IllegalStateException("A site url is multiple times in the db! $url")
            }?.toSiteModel()
        }
    }

    fun getCount(): Long
    {
        return transaction(db) {
            SiteEntity.count()
        }
    }

    fun saveTo(file: File, type: String)
    {

    }

}

