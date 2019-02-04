package com.psoffritti.librarysampleapptemplate.core

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.RelativeLayout
import java.lang.RuntimeException

class ProgressBarWebView: RelativeLayout {
    private val webView: WebView
    private val progressbar: View
    var onUrlClick: (String) -> Unit = { throw RuntimeException() }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        webView = WebView(context)
        progressbar = ProgressBar(context, null, android.R.attr.progressBarStyle)

        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)

        addView(webView, RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        addView(progressbar, layoutParams)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView, url: String) {
                super.onPageCommitVisible(view, url)
                progressbar.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return handleUrl(url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    handleUrl(request.url.toString())
                else
                    return false
            }

            private fun handleUrl(url: String): Boolean {
                return if (url.startsWith("http://") || url.startsWith("https://")) {
                    onUrlClick(url)
                    true
                } else
                    false
            }
        }
    }

    fun loadUrl(url: String?) {
        webView.loadUrl(url)
    }

    fun enableJavascript(enable: Boolean) {
        webView.settings.javaScriptEnabled = enable
    }
}