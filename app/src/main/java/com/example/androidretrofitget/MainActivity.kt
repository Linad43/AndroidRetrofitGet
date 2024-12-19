package com.example.androidretrofitget

import android.annotation.SuppressLint
import android.os.Bundle
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    private lateinit var process: ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var imageIV: ImageView
    private lateinit var updateBTN: Button

    @SuppressLint("UseSupportActionBar", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        toolbar = findViewById(R.id.toolbar)
        imageIV = findViewById(R.id.imageIV)
        updateBTN = findViewById(R.id.updateBTN)
        process = findViewById(R.id.progress_circular)
        setSupportActionBar(toolbar)
        loadRandomDogImage()
//        GlobalScope.async {
//            val response = RetrofitInstance.api.getRandomDog()
//            Glide.with(this@MainActivity)
//                .load(response.url)
//                .into(imageIV)
//            process.visibility=View.INVISIBLE
//        }
        updateBTN.setOnClickListener {
            loadRandomDogImage()
//            process.visibility=View.VISIBLE
//            GlobalScope.async {
//                Glide.with(applicationContext)
//                    .load(RetrofitInstance.api.getRandomDog().url)
//                    .into(imageIV)
//                process.visibility=View.INVISIBLE
//            }
        }
    }
    private fun loadRandomDogImage() {
        process.visibility = View.VISIBLE
        imageIV.visibility = View.INVISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getRandomDog()
                withContext(Dispatchers.Main) {
                    Glide.with(this@MainActivity)
                        .load(response.url)
                        .listener(object : RequestListener<Drawable> {

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                process.visibility = View.GONE
                                imageIV.visibility = View.VISIBLE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                process.visibility = View.GONE
                                imageIV.visibility = View.VISIBLE
                                return false
                            }
                        })
                        .into(imageIV)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    process.visibility = View.GONE
                    imageIV.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun update() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getRandomDog()
            } catch (e: HttpException) {
                Toast.makeText(
                    applicationContext,
                    "http error",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            } catch (e: IOException) {
                Toast.makeText(
                    applicationContext,
                    "IO error",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            imageIV.setImageURI(response.url.toUri())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit -> {
                finishAffinity()
            }
        }
        return true
    }
}