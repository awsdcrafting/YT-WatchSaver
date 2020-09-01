package eu.scisneromam.config

import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigValueFactory
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.Writer
import com.uchuhimo.konf.source.base.toHierarchicalMap
import java.io.OutputStream

/**
 * Custom Hocon writer (Copy of Default but with comments enabled)
 *
 */
class CommentedHoconWriter(val config: Config) : Writer
{
    private val renderOpts = ConfigRenderOptions.defaults()
        .setComments(true)
        .setFormatted(true)
        .setOriginComments(false)
        .setJson(false)

    override fun toWriter(writer: java.io.Writer)
    {
        writer.write(toText())
    }

    override fun toOutputStream(outputStream: OutputStream)
    {
        outputStream.writer().use {
            toWriter(it)
        }
    }

    override fun toText(): String
    {
        return ConfigValueFactory.fromMap(config.toHierarchicalMap()).render(renderOpts)
            .replace("\n", System.lineSeparator())
    }
}

val Config.toCommentedHocon: Writer get() = CommentedHoconWriter(this)
