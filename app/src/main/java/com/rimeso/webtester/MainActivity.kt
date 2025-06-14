package com.rimeso.webtester

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val urlInput = findViewById<AutoCompleteTextView>(R.id.urlInput)
        val testButton = findViewById<Button>(R.id.testButton)
        val jsSwitch = findViewById<android.widget.Switch>(R.id.jsSwitch)
        val zoomSwitch = findViewById<android.widget.Switch>(R.id.zoomSwitch)
        val fullScreenSwitch = findViewById<android.widget.Switch>(R.id.fullScreenSwitch)
        val statusBarSwitch = findViewById<android.widget.Switch>(R.id.statusBarSwitch)
        val darkModeSwitch = findViewById<android.widget.Switch>(R.id.darkModeSwitch)
        val cacheSwitch = findViewById<android.widget.Switch>(R.id.cacheSwitch)
        val fileAccessSwitch = findViewById<android.widget.Switch>(R.id.fileAccessSwitch)
        val mixedContentSwitch = findViewById<android.widget.Switch>(R.id.mixedContentSwitch)
        val userAgentInput = findViewById<EditText>(R.id.userAgentInput)
        val jsInjectionInput = findViewById<EditText>(R.id.jsInjectionInput)
        val responsiveSpinner = findViewById<android.widget.Spinner>(R.id.responsiveSpinner)
        val clearCookiesButton = findViewById<Button>(R.id.clearCookiesButton)
        val belowStatusBarSwitch = findViewById<android.widget.Switch>(R.id.belowStatusBarSwitch)
        val versionText = findViewById<TextView>(R.id.versionText)
        val statusBarColorSwitch = findViewById<android.widget.Switch>(R.id.statusBarColorSwitch)

        // URL suggestions (simple SharedPreferences based)
        val prefs = getSharedPreferences("webviewtester", MODE_PRIVATE)
        val urlHistory = prefs.getStringSet("urlHistory", setOf())?.toMutableSet() ?: mutableSetOf()
        val urlAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, urlHistory.toList())
        urlInput.setAdapter(urlAdapter)
        urlInput.threshold = 1

        // Show version/build number
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionText.text = "v${pInfo.versionName} (${pInfo.versionCode})"
        } catch (_: PackageManager.NameNotFoundException) {}

        clearCookiesButton.setOnClickListener {
            android.webkit.CookieManager.getInstance().removeAllCookies(null)
            android.webkit.CookieManager.getInstance().flush()
            android.widget.Toast.makeText(this, "Cookies/data cleared", android.widget.Toast.LENGTH_SHORT).show()
        }

        testButton.setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) {
                urlHistory.add(url)
                prefs.edit().putStringSet("urlHistory", urlHistory).apply()
                urlAdapter.clear()
                urlAdapter.addAll(urlHistory)
                urlAdapter.notifyDataSetChanged()
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra("URL", url)
                intent.putExtra("JS_ENABLED", jsSwitch.isChecked)
                intent.putExtra("ZOOM_ENABLED", zoomSwitch.isChecked)
                intent.putExtra("FULL_SCREEN", fullScreenSwitch.isChecked)
                intent.putExtra("SHOW_STATUS_BAR", statusBarSwitch.isChecked)
                intent.putExtra("DARK_MODE", darkModeSwitch.isChecked)
                intent.putExtra("CACHE_ENABLED", cacheSwitch.isChecked)
                intent.putExtra("FILE_ACCESS", fileAccessSwitch.isChecked)
                intent.putExtra("MIXED_CONTENT", mixedContentSwitch.isChecked)
                intent.putExtra("USER_AGENT", userAgentInput.text.toString())
                intent.putExtra("JS_INJECTION", jsInjectionInput.text.toString())
                intent.putExtra("RESPONSIVE_MODE", responsiveSpinner.selectedItemPosition)
                intent.putExtra("SHOW_BELOW_STATUS_BAR", belowStatusBarSwitch.isChecked)
                intent.putExtra("LIGHT_STATUS_BAR_TEXT", statusBarColorSwitch.isChecked)
                startActivity(intent)
            } else {
                urlInput.error = "Please enter a valid URL (e.g., https://example.com)"
            }
        }
    }
}