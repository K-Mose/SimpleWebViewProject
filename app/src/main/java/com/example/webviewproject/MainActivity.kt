package com.example.webviewproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.webviewproject.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val url: String = if (intent.hasExtra("URL"))
            intent.getStringExtra("URL")!!
         else "http://www.google.com"
        binding.webView.apply {
            webViewClient= WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(url)
        }
        binding.lyMainContent.navBottom.setOnNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.nav_naver -> {
                    binding.webView.loadUrl("https://www.naver.com")
                }
                R.id.nav_daum -> {
                    binding.webView.loadUrl("https://www.daum.net")
                }
                R.id.nav_google -> {
                    binding.webView.loadUrl("https://www.google.com")
                }
            }
            true
        }

    }

}