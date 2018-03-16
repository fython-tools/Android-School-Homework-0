package me.fython.schoolexam.ui

import android.Manifest
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import me.fython.schoolexam.R
import me.fython.schoolexam.model.Photo
import me.fython.schoolexam.util.OkHttpUtils
import okhttp3.Request
import java.io.File

class ViewerActivity : AppCompatActivity() {

    private val imageView by lazy { findViewById<ImageView>(android.R.id.background) }

    private lateinit var data: Photo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)

        setSupportActionBar(findViewById(R.id.toolbar))

        imageView.transitionName = TRANSITION_IMAGE

        data = intent.getParcelableExtra(EXTRA_DATA)

        title = "id:" + data.id
        supportActionBar?.subtitle = data.description
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Picasso.with(this).load(data.urls.small).into(imageView)
    }

    private fun save() {
        async(UI) {
            val fileName = Uri.parse(data.urls.full).lastPathSegment + ".jpg"
            val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    fileName)
            val dialog = ProgressDialog.show(this@ViewerActivity, "Saving...",
                    file.toString(), true, false)
            try {
                async(CommonPool) {
                    OkHttpUtils.newCall(Request.Builder().url(data.urls.full).build())
                            .execute()
                            .let {
                                if (it.isSuccessful && it.code() == 200) {
                                    file.createNewFile()
                                    file.outputStream().run {
                                        write(it.body()!!.bytes())
                                        flush()
                                        close()
                                    }
                                }
                            }
                }.await()
                val intent = Intent(Intent.ACTION_VIEW)
                val uri = FileProvider.getUriForFile(
                        this@ViewerActivity,
                        "me.fython.schoolexam.fileprovider", file)
                intent.setDataAndType(uri, "image/*")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivity(Intent.createChooser(intent, "Choose app to open"))
            } catch (e : Exception) {
                e.printStackTrace()
            }
            dialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_viewer, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        if (item?.itemId == R.id.action_save) {
            if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1)
            } else {
                save()
            }
            return true
        }
        if (item?.itemId == R.id.action_set) {
            async(UI) {
                val fileName = Uri.parse(data.urls.full).lastPathSegment + ".jpg"
                val file = File(externalCacheDir, fileName)
                val dialog = ProgressDialog.show(this@ViewerActivity, "Saving...",
                        null, true, false)
                try {
                    async(CommonPool) {
                        OkHttpUtils.newCall(Request.Builder().url(data.urls.full).build())
                                .execute()
                                .let {
                                    if (it.isSuccessful && it.code() == 200) {
                                        file.createNewFile()
                                        file.outputStream().run {
                                            write(it.body()!!.bytes())
                                            flush()
                                            close()
                                        }
                                    }
                                }
                    }.await()
                    val uri = FileProvider.getUriForFile(
                            this@ViewerActivity,
                            "me.fython.schoolexam.fileprovider", file)
                    startActivity(WallpaperManager.getInstance(this@ViewerActivity)
                            .getCropAndSetWallpaperIntent(uri))
                } catch (e : Exception) {
                    e.printStackTrace()
                }
                dialog.dismiss()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 0 && permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                save()
            } else {
                Toast.makeText(this,
                        "Please grant permission before saving", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {

        const val TRANSITION_IMAGE = "image"

        const val EXTRA_DATA = "data"

    }

}