package eu.scisneromam


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.features.ContentNegotiation
import io.ktor.features.ContentTransformationException
import io.ktor.features.suitableCharset
import io.ktor.gson.GsonConverter
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CopyableThrowable
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 * Custom Gson Converter based on [GsonConverter]
 */
class CustomGsonConverter(val gson: Gson = Gson()) : ContentConverter
{
    val listType = object : TypeToken<List<Param?>?>()
    {}.type

    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any
    ): Any?
    {
        return TextContent(gson.toJson(value), contentType.withCharset(context.call.suitableCharset()))
    }

    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any?
    {
        val request = context.subject
        val channel = request.value as? ByteReadChannel ?: return null
        val reader = channel.toInputStream().reader(context.call.request.contentCharset() ?: Charsets.UTF_8)
        val text = reader.readText().trim()
        val type = request.type

        if (gson.isExcluded(type))
        {
            throw ExcludedTypeGsonException(type)
        }

        return parseJson(text, gson, type.javaObjectType)
    }


    private data class Param(val key: String, val value: Any)
    private class Params
    {
        val map: MutableMap<String, Any> = HashMap()
        fun toJson(gson: Gson): String
        {
            return gson.toJson(map)
        }
    }

    private fun List<Param>.toParams(): Params
    {
        val params = Params()
        val map = params.map
        for (param in this)
        {
            val key = param.key
            when (val mapValue = map[key])
            {
                null ->
                {
                    map[key] = param.value
                }
                is Collection<*> ->
                {
                    val collection = ArrayList<Any>(mapValue)
                    collection.add(param.value)
                    map[key] = collection
                }
                else             ->
                {
                    val collection = ArrayList<Any>()
                    collection.add(mapValue)
                    collection.add(param.value)
                    map[key] = collection
                }
            }
        }

        return params
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> parseJson(json: String, gson: Gson = this.gson, type: Class<*>): T
    {
        println("Converting: $json")
        return try
        {
            println("converting per default")
            gson.fromJson(json, type)
        } catch (e: java.lang.Exception)
        {
            println("converting per params")
            val paramList = gson.fromJson<List<Param>>(json, listType)
            val params = paramList.toParams()
            val pJson = params.toJson(gson)
            gson.fromJson(pJson, type)
        } as T
    }
}

/**
 * Register GSON to [ContentNegotiation] feature
 */
fun ContentNegotiation.Configuration.cgson(
    contentType: ContentType = ContentType.Application.Json,
    block: GsonBuilder.() -> Unit = {}
): CustomGsonConverter
{
    val builder = GsonBuilder()
    builder.apply(block)
    val converter = CustomGsonConverter(builder.create())
    register(contentType, converter)
    return converter
}

internal class ExcludedTypeGsonException(
    val type: KClass<*>
) : Exception("Type ${type.jvmName} is excluded so couldn't be used in receive"),
    CopyableThrowable<ExcludedTypeGsonException>
{

    override fun createCopy(): ExcludedTypeGsonException? = ExcludedTypeGsonException(type).also {
        it.initCause(this)
    }
}

internal class UnsupportedNullValuesException :
    ContentTransformationException("Receiving null values is not supported")

private fun Gson.isExcluded(type: KClass<*>) =
    excluder().excludeClass(type.java, false)

