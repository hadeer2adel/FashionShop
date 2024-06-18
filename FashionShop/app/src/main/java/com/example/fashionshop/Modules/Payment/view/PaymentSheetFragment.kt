package com.example.fashionshop.Modules.Payment.view

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.fashionshop.R

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentSheetFragment : BottomSheetDialogFragment() {

    private lateinit var webView: WebView

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
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        val paymentUrl = arguments?.getString("paymentUrl")
        paymentUrl?.let {
            webView.loadUrl(it)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        navigateToSettingsFragment()
        showToast("Payment successful")
    }

    private fun navigateToSettingsFragment() {
        findNavController().navigate(R.id.actiomfromSheet_to_order)

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
