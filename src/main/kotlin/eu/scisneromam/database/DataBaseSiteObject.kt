package eu.scisneromam.database

import eu.scisneromam.models.SiteModel
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object SiteTable : LongIdTable()
{
    val siteTitle = varchar("site_name", 150)
    val siteUrl = varchar("site_url", 256)
    val times = integer("times")
}

class SiteEntity(id: EntityID<Long>) : LongEntity(id)
{
    companion object : LongEntityClass<SiteEntity>(SiteTable)

    var siteTitle by SiteTable.siteTitle
    var siteUrl by SiteTable.siteUrl
    var times by SiteTable.times

    fun toSiteModel(): SiteModel = SiteModel(siteTitle, siteUrl, times)
}