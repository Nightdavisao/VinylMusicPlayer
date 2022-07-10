package com.poupa.vinylmusicplayer.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.poupa.vinylmusicplayer.R

class GrantAppPermissionsActivity : AppCompatActivity() {
    private lateinit var grantAllFilesTextView: TextView
    private lateinit var grantAllFilesPermButton: Button
    private lateinit var grantLegacyStoragePermissionButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grant_app_permissions)
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (checkPermissionsGranted()) {
                finish()
            } else {
                Toast.makeText(this, "Without this permission granted, we can't play audio files.", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
        }
        grantAllFilesTextView = findViewById(R.id.permission_all_files_text)
        grantAllFilesPermButton = findViewById(R.id.grant_all_files_permission_btn)
        grantLegacyStoragePermissionButton = findViewById(R.id.grant_legacy_storage_access_btn)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            grantAllFilesTextView.visibility = View.GONE
            grantAllFilesPermButton.visibility = View.GONE
        }
        grantAllFilesPermButton.setOnClickListener {
            if (!isAllFilesPermGranted()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            checkPermissionsGrantedAndFinish()
        }
        grantLegacyStoragePermissionButton.setOnClickListener {
            if (!isLegacyPermGranted()) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
            }
            checkPermissionsGrantedAndFinish()
        }
    }

    private fun checkPermissionsGrantedAndFinish() {
        if (checkPermissionsGranted()) {
            finish()
        }
    }

    private fun checkPermissionsGranted(): Boolean {
        val isAllFilesPermGranted = isAllFilesPermGranted()
        val isLegacyPermGranted = isLegacyPermGranted()
        if (isAllFilesPermGranted) {
            grantAllFilesPermButton.isClickable = false
            grantAllFilesPermButton.text = "Granted!"
        }
        if (isLegacyPermGranted) {
            grantLegacyStoragePermissionButton.isClickable = false
            grantLegacyStoragePermissionButton.text = "Granted!"
        }

        if (isAllFilesPermGranted && isLegacyPermGranted) {
            return true
        }
        return false
    }

    private fun isAllFilesPermGranted() = Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()
    private fun isLegacyPermGranted() = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    override fun onRestart() {
        super.onRestart()
        checkPermissionsGranted()
    }
}