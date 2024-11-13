package com.example.androidretrofitget

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var imageIV: ImageView
    private lateinit var updateBTN: Button

    @SuppressLint("UseSupportActionBar")
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
        setSupportActionBar(toolbar)
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
        updateBTN.setOnClickListener {
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