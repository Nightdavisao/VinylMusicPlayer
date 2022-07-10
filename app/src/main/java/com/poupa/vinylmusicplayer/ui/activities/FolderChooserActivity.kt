package com.poupa.vinylmusicplayer.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.storage.StorageManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.poupa.vinylmusicplayer.R

class FolderChooserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.folder_chooser_toolbar))
        setContentView(R.layout.activity_folder_chooser)
        val spinner = findViewById<Spinner>(R.id.storage_volume_spinner)
        val storageService = getSystemService(StorageManager::class.java) as StorageManager
        val storageVolumes = storageService.storageVolumes.filter { it.directory != null }
        val rootVolumes = storageVolumes.map { it.directory!!.absolutePath }
        val items = storageVolumes.map { storageVolume ->
            if (storageVolume.isPrimary) {
                return@map "Internal Storage"
            }
            return@map storageVolume.uuid ?: if (storageVolume.isRemovable) {
                "Removable Media"
            } else {
                "Unknown UUID"
            }
        }
        val itemsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter = itemsAdapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(this@FolderChooserActivity, rootVolumes[p2], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
}