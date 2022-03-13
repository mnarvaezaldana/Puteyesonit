package com.yucatancorp.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager

class GaleryActivity : AppCompatActivity() {

    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private var recyclerView: RecyclerView? = null
    private var uris = ArrayList<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galery)
        recyclerView = findViewById(R.id.rvGallery)
        val permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted
        }
        updateRequestPermissions(permissionsLauncher)
    }

    private fun updateRequestPermissions(permissionsLauncher: ActivityResultLauncher<Array<String>>) {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission

        val permissionsToRequest = mutableListOf<String>()
        if(!writePermissionGranted) permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(!readPermissionGranted) permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        if(permissionsToRequest.isNotEmpty()) permissionsLauncher.launch(permissionsToRequest.toTypedArray())

        displayImages()
    }

    private fun displayImages(){
        val imageProjection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media._ID,
        )
        val imageSortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            imageSortOrder
        )
        cursor.use {
            it?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val size = it.getString(sizeColumn)
                    val date = it.getString(dateColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    uris.add(contentUri)
                }
                val adapter = GalleryAdapter(uris, this)
                recyclerView?.adapter = adapter
                recyclerView?.layoutManager = GridLayoutManager(this, 3)
            } ?: kotlin.run {
                Log.e("TAG", "Cursor is null!")
            }
        }
    }
}