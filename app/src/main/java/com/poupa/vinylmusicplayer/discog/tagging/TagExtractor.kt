package com.poupa.vinylmusicplayer.discog.tagging

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.poupa.vinylmusicplayer.model.Song
import com.simplecityapps.ktaglib.KTagLib
import java.io.File
import java.lang.Exception

/**
 * @author SC (soncaokim)
 */
class TagExtractor(val context: Context) {
    private val kTagLib = KTagLib()

    private fun safeGetYear(propertyMap: Map<String, List<String>>, default: Int): Int {
        val yearField = propertyMap["YEAR"]?.firstOrNull()?.toIntOrNull()
        // YYYY-MM-DD
        val dateField = propertyMap["DATE"]?.firstOrNull()
            ?.substringBefore("-")
            ?.substringBefore(".")?.toIntOrNull()
        if (yearField != null) {
            return yearField
        }
        if (dateField != null) {
            return dateField
        }
        return default
    }

    fun extractTags(song: Song) {
        try {
            // Override with metadata extracted from the file ourselves
            val file = File(song.data)
            context.contentResolver.openFileDescriptor(Uri.fromFile(file), "r")?.use {
                val tagFile = kTagLib.getMetadata(it.detachFd())
                if (tagFile != null) {
                    song.albumName = tagFile.propertyMap["ALBUM"]?.firstOrNull() ?: song.albumName
                    song.artistNames = tagFile.propertyMap["ARTIST"] ?: song.artistNames
                    song.albumArtistNames = tagFile.propertyMap["ALBUMARTIST"] ?: song.albumArtistNames
                    song.title = tagFile.propertyMap["TITLE"]?.firstOrNull() ?: song.title
                    song.genre = tagFile.propertyMap["GENRE"]?.firstOrNull() ?: song.genre
                    song.discNumber = tagFile.propertyMap["DISCNUMBER"]?.firstOrNull()?.substringBefore('/')?.toIntOrNull() ?: song.discNumber
                    song.trackNumber = tagFile.propertyMap["TRACKNUMBER"]?.firstOrNull()?.substringBefore('/')?.toIntOrNull() ?: song.trackNumber
                    song.year = safeGetYear(tagFile.propertyMap, song.year)
                    // is it consistent with every audio format?
                    song.replayGainAlbum = tagFile.propertyMap["REPLAYGAIN_ALBUM_PEAK"]?.firstOrNull()?.toFloatOrNull() ?: song.replayGainAlbum
                    song.replayGainTrack = tagFile.propertyMap["REPLAYGAIN_TRACK_PEAK"]?.firstOrNull()?.toFloatOrNull() ?: song.replayGainTrack
                }
            }
            //val rgValues = ReplayGainTagExtractor.setReplayGainValues(file)
            //song.replayGainAlbum = rgValues.album
            //song.replayGainTrack = rgValues.track
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: NoSuchMethodError) {
            e.printStackTrace()
        } catch (e: VerifyError) {
            e.printStackTrace()
        }
    }
}