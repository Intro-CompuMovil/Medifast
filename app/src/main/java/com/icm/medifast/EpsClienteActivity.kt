package com.icm.medifast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class EpsClienteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eps_cliente)

        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.loadUrl("https://www.epssanitas.com/usuarios/web/nuevo-portal-eps")

    }
}