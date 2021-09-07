package com.e.dreamcardseogu

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import com.e.dreamcardseogu.databinding.FragmentCardBinding

class CardFragment : Fragment() {
    private lateinit var mProgressBar: ProgressBar
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCardBinding.inflate(inflater,container,false)
        webView = binding.cardwebview
        mProgressBar = binding.progress1
        webView.apply{
            webViewClient = WebViewClientClass()
            webChromeClient = WebChromeClient()

            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true                                      /*자바스크립트 허용 */
            settings.setSupportMultipleWindows(false)                            /* 새창 띄우기 허용 */
            settings.javaScriptCanOpenWindowsAutomatically = true             /*자바스크립트 새창 허용 */
            settings.loadWithOverviewMode = true
            settings.allowContentAccess = true;
        }
        var url = getString(R.string.card_url)
        webView.loadUrl(url)
        return binding.root
    }

    inner class WebViewClientClass: WebViewClient() {
        /* 새로운 url로 이동할때 */
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            return true
        }

        /* 페이지 시작 시 프로그레스바만 보이도록 설정 */
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mProgressBar.visibility = ProgressBar.VISIBLE
            webView.visibility = View.INVISIBLE
        }

        /* 페이지 로딩끝났을때 */
        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            mProgressBar.visibility = ProgressBar.GONE
            webView.visibility = View.VISIBLE
        }

        /* error 처리 */
        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
        }
    }

}