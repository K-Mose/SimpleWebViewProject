package com.example.webviewproject

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.webviewproject.databinding.ActivityNakedMarketBinding

class NakedMarket : AppCompatActivity() {
    lateinit var binding: ActivityNakedMarketBinding
    private var cookieManager: CookieManager = CookieManager.getInstance()
    private lateinit var downLoadManager: DownloadManager

    // 권한 request 추가
    var permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result == true) {
            // 퍼미션 승인
            Toast.makeText(this@NakedMarket, "Permission Granted", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this@NakedMarket, "Permission Denied", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNakedMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebViewChromeClient()
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowContentAccess = true
                javaScriptCanOpenWindowsAutomatically = true
            }

            setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                downLoadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                Log.i("다운로드리스너", "다운로드 클릭")
                if(ContextCompat.checkSelfPermission(this@NakedMarket, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        // https://androidride.com/android-webview-example-tutorial-kotlin-java-download-source-code/#android%20webview%20download%20file%20example
                        val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
                        var request = DownloadManager.Request(Uri.parse(url))
                            .apply {
                                setAllowedOverMetered(true)
                                addRequestHeader("User-Agent", userAgent)
                                addRequestHeader("Cookie", cookieManager.getCookie("url"))
                                setMimeType(mimetype)
                                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                                setTitle(fileName)
                            }
                        val builder = AlertDialog.Builder(this@NakedMarket).apply {
                            setTitle(fileName)
                            setMessage("다운로드 하시겠습니까?")
                            setPositiveButton("네") { dialog, _ ->
                                downLoadManager.enqueue(request)
                                dialog.dismiss()
                            }
                            setNegativeButton("아니요") { dialog, _ ->
                                dialog.dismiss()
                            }
                        }
                        builder.show()
                        Log.i("다운로드리스너", "다운로드 종료")
                    } catch (e: Exception) {
                        Toast.makeText(this@NakedMarket, "다운로드 실패 ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("다운로드리스터", "에러 발생 ${e.message}")
                        e.printStackTrace()
                    }
                    //startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))

                } else {
                    permissionRequest.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            loadUrl("https://nakedmarket.co.kr/index")
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e("onPause::","Pause")
        binding.webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume::","Resume")
    }

    inner class WebViewChromeClient : WebChromeClient() {

        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
            Log.e("ONCREATEWINDOW","creat window")
        }

        override fun onCloseWindow(window: WebView?) {
            super.onCloseWindow(window)

            binding.webView.removeView(window!!)
        }
    }
}