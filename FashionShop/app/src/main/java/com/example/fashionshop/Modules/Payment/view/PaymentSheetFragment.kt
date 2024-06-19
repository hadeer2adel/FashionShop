package com.example.fashionshop.Modules.Payment.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fashionshop.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentSheetFragment : BottomSheetDialogFragment() {

    private lateinit var webView: WebView
    private val successUrl = "https://example.com/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.startsWith(successUrl)) {
                    handleSuccess()
                    return true
                }
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.startsWith(successUrl)) {
                    handleSuccess()
                    return true
                }
                return false
            }
        }
        webView.webChromeClient = WebChromeClient()

        val paymentUrl = arguments?.getString("paymentUrl")
        paymentUrl?.let {
            webView.loadUrl(it)
        }
    }

    private fun handleSuccess() {
        showToast("Payment successful")
        dismiss()
        findNavController().navigate(R.id.actiomfromSheet_to_order)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        showToast("Payment process finished")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(paymentUrl: String): PaymentSheetFragment {
            val fragment = PaymentSheetFragment()
            val args = Bundle()
            args.putString("paymentUrl", paymentUrl)
            fragment.arguments = args
            return fragment
        }
    }
}
