package com.poupa.vinylmusicplayer.discog.tagging

import org.jaudiotagger.audio.AudioFile
import com.poupa.vinylmusicplayer.discog.tagging.ReplayGainTagExtractor.ReplayGainValues
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag
import org.jaudiotagger.tag.flac.FlacTag
import com.poupa.vinylmusicplayer.discog.tagging.ReplayGainTagExtractor
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.mp4.Mp4Tag
import kotlin.Throws
import org.jaudiotagger.tag.TagField
import java.io.IOException
import java.io.RandomAccessFile
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*

// TODO: use ktaglib for extraction
object ReplayGainTagExtractor {
    // Normalize all tags using the Vorbis ones
    private const val REPLAYGAIN_TRACK_GAIN = "REPLAYGAIN_TRACK_GAIN"
    private const val REPLAYGAIN_ALBUM_GAIN = "REPLAYGAIN_ALBUM_GAIN"
    fun setReplayGainValues(file: AudioFile): ReplayGainValues {
        var tags: Map<String?, Float?>? = null
        try {
            val tag = file.tag
            tags = if (tag is VorbisCommentTag || tag is FlacTag) {
                parseTags(tag)
            } else if (tag is Mp4Tag) {
                parseMp4Tags(tag)
            } else {
                parseId3Tags(tag, file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val result = ReplayGainValues()
        if (tags != null && !tags.isEmpty()) {
            if (tags.containsKey(REPLAYGAIN_TRACK_GAIN)) {
                result.track = tags[REPLAYGAIN_TRACK_GAIN]!!
            }
            if (tags.containsKey(REPLAYGAIN_ALBUM_GAIN)) {
                result.album = tags[REPLAYGAIN_ALBUM_GAIN]!!
            }
        }
        return result
    }

    @Throws(Exception::class)
    private fun parseId3Tags(tag: Tag, file: AudioFile): Map<String?, Float?> {
        var id: String? = null
        if (tag.hasField("TXXX")) {
            id = "TXXX"
        } else if (tag.hasField("RGAD")) {    // may support legacy metadata formats: RGAD, RVA2
            id = "RGAD"
        } else if (tag.hasField("RVA2")) {
            id = "RVA2"
        }
        if (id == null) return parseLameHeader(file)
        val tags: MutableMap<String?, Float?> = HashMap()
        for (field in tag.getFields(id)) {
            val data = field.toString().split(";").toTypedArray()
            data[0] = data[0].substring(13, data[0].length - 1).uppercase(Locale.getDefault())
            if (data[0] == "TRACK") {
                data[0] = REPLAYGAIN_TRACK_GAIN
            } else if (data[0] == "ALBUM") {
                data[0] = REPLAYGAIN_ALBUM_GAIN
            }
            tags[data[0]] = parseFloat(data[1])
        }
        return tags
    }

    private fun parseTags(tag: Tag): MutableMap<String?, Float?> {
        val tags: MutableMap<String?, Float?> = HashMap()
        if (tag.hasField(REPLAYGAIN_TRACK_GAIN)) {
            tags[REPLAYGAIN_TRACK_GAIN] =
                parseFloat(tag.getFirst(REPLAYGAIN_TRACK_GAIN))
        }
        if (tag.hasField(REPLAYGAIN_ALBUM_GAIN)) {
            tags[REPLAYGAIN_ALBUM_GAIN] =
                parseFloat(tag.getFirst(REPLAYGAIN_ALBUM_GAIN))
        }
        return tags
    }

    private fun parseMp4Tags(tag: Tag): Map<String?, Float?> {
        val tags = parseTags(tag)
        val ITUNES_PREFIX = "----:com.apple.iTunes:"
        if (!tags.containsKey(REPLAYGAIN_TRACK_GAIN) && tag.hasField(ITUNES_PREFIX + REPLAYGAIN_TRACK_GAIN)) {
            tags[REPLAYGAIN_TRACK_GAIN] =
                parseFloat(tag.getFirst(ITUNES_PREFIX + REPLAYGAIN_TRACK_GAIN))
        }
        if (!tags.containsKey(REPLAYGAIN_ALBUM_GAIN) && tag.hasField(ITUNES_PREFIX + REPLAYGAIN_ALBUM_GAIN)) {
            tags[REPLAYGAIN_ALBUM_GAIN] =
                parseFloat(tag.getFirst(ITUNES_PREFIX + REPLAYGAIN_ALBUM_GAIN))
        }
        return tags
    }

    @Throws(IOException::class)
    private fun parseLameHeader(file: AudioFile): Map<String?, Float?> {
        // Method taken from adrian-bl/bastp library
        val tags: MutableMap<String?, Float?> = HashMap()
        val s = RandomAccessFile(file.file, "r")
        val chunk = ByteArray(12)
        s.seek(0x24)
        s.read(chunk)
        val lameMark = String(chunk, 0, 4, Charset.forName("ISO-8859-1"))
        if (lameMark == "Info" || lameMark == "Xing") {
            s.seek(0xAB)
            s.read(chunk)
            val raw = b2be32(chunk)
            val gtrk_raw = raw shr 16 /* first 16 bits are the raw track gain value */
            val galb_raw = raw and 0xFFFF /* the rest is for the album gain value       */
            var gtrk_val = (gtrk_raw and 0x01FF).toFloat() / 10
            var galb_val = (galb_raw and 0x01FF).toFloat() / 10
            gtrk_val = if (gtrk_raw and 0x0200 != 0) -1 * gtrk_val else gtrk_val
            galb_val = if (galb_raw and 0x0200 != 0) -1 * galb_val else galb_val
            if (gtrk_raw and 0xE000 == 0x2000) {
                tags[REPLAYGAIN_TRACK_GAIN] = gtrk_val
            }
            if (gtrk_raw and 0xE000 == 0x4000) {
                tags[REPLAYGAIN_ALBUM_GAIN] = galb_val
            }
        }
        return tags
    }

    private fun b2le32(b: ByteArray): Int {
        var r = 0
        for (i in 0..3) {
            r = r or (b2u(b[i]) shl 8 * i)
        }
        return r
    }

    private fun b2be32(b: ByteArray): Int {
        return swap32(b2le32(b))
    }

    private fun swap32(i: Int): Int {
        return (i and 0xff shl 24) + (i and 0xff00 shl 8) + (i and 0xff0000 shr 8) + (i shr 24 and 0xff)
    }

    private fun b2u(x: Byte): Int {
        return x.toInt() and 0xFF
    }

    private fun parseFloat(s: String): Float {
        var s = s
        var result = 0.0f
        try {
            s = s.replace("[^0-9.-]".toRegex(), "")
            result = s.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    class ReplayGainValues {
        var track = 0f
        var album = 0f
    }
}