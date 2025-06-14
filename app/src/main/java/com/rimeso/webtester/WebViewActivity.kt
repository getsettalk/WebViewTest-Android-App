package com.rimeso.webtester

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webView = findViewById(R.id.webView)

        // Get parameters from intent
        val url = intent.getStringExtra("URL") ?: "https://google.com"
        val jsEnabled = intent.getBooleanExtra("JS_ENABLED", true)
        val zoomEnabled = intent.getBooleanExtra("ZOOM_ENABLED", true)
        val fullScreen = intent.getBooleanExtra("FULL_SCREEN", false)
        val showStatusBar = intent.getBooleanExtra("SHOW_STATUS_BAR", true)
        val darkMode = intent.getBooleanExtra("DARK_MODE", false)
        val cacheEnabled = intent.getBooleanExtra("CACHE_ENABLED", true)
        val fileAccess = intent.getBooleanExtra("FILE_ACCESS", false)
        val mixedContent = intent.getBooleanExtra("MIXED_CONTENT", false)
        val userAgent = intent.getStringExtra("USER_AGENT") ?: ""
        val jsInjection = intent.getStringExtra("JS_INJECTION") ?: ""
        val responsiveMode = intent.getIntExtra("RESPONSIVE_MODE", 0)
        val showBelowStatusBar = intent.getBooleanExtra("SHOW_BELOW_STATUS_BAR", false)
        val lightStatusBarText = intent.getBooleanExtra("LIGHT_STATUS_BAR_TEXT", true)

        // Full screen and status bar handling
        if (fullScreen) {
            window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        } else if (!showStatusBar) {
            window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            // Show status bar (default)
            window.decorView.systemUiVisibility = if (lightStatusBarText) {
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                0
            }
            actionBar?.show()
        }

        // Show WebView below status bar if requested
        if (showBelowStatusBar) {
            val params = webView.layoutParams as android.view.ViewGroup.MarginLayoutParams
            val statusBarResId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (statusBarResId > 0) {
                params.topMargin = resources.getDimensionPixelSize(statusBarResId)
                webView.layoutParams = params
            }
        }

        // Dark mode
        if (darkMode) {
            webView.setBackgroundColor(android.graphics.Color.BLACK)
        }

        // Configure WebView
        val settings = webView.settings
        settings.javaScriptEnabled = jsEnabled
        settings.setSupportZoom(zoomEnabled)
        settings.builtInZoomControls = zoomEnabled
        settings.displayZoomControls = false
        settings.cacheMode = if (cacheEnabled) android.webkit.WebSettings.LOAD_DEFAULT else android.webkit.WebSettings.LOAD_NO_CACHE
        settings.allowFileAccess = fileAccess
        if (userAgent.isNotBlank()) settings.userAgentString = userAgent
        if (mixedContent) settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        // Responsive mode (set viewport width)
        when (responsiveMode) {
            1 -> webView.layoutParams.width = (360 * resources.displayMetrics.density).toInt() // Mobile
            2 -> webView.layoutParams.width = (768 * resources.displayMetrics.density).toInt() // Tablet
            3 -> webView.layoutParams.width = (1200 * resources.displayMetrics.density).toInt() // Desktop
            else -> webView.layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
        }

        // Set WebViewClient to handle page loading and errors
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                // Inject custom JS if provided
                if (jsInjection.isNotBlank()) {
                    webView.evaluateJavascript(jsInjection, null)
                }
                android.widget.Toast.makeText(this@WebViewActivity, "Content loaded", android.widget.Toast.LENGTH_SHORT).show()
            }
            override fun onReceivedError(
                view: WebView?,
                request: android.webkit.WebResourceRequest?,
                error: android.webkit.WebResourceError?
            ) {
                android.widget.Toast.makeText(this@WebViewActivity, "Error loading page: ${error?.description}", android.widget.Toast.LENGTH_LONG).show()
            }
        }

        // Load the URL
        try {
            webView.loadUrl(url)
        } catch (e: Exception) {
            android.widget.Toast.makeText(this, "Failed to load URL: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }
}