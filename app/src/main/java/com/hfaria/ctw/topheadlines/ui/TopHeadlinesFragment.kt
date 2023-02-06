package com.hfaria.ctw.topheadlines.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.hfaria.ctw.topheadlines.BuildConfig
import com.hfaria.ctw.topheadlines.R
import com.hfaria.ctw.topheadlines.databinding.FragmentTopHeadlinesBinding
import com.hfaria.ctw.topheadlines.ui.article_content.ArticleContentActivity
import com.hfaria.ctw.topheadlines.ui.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TopHeadlinesFragment : BaseFragment<TopHeadlinesViewModel>() {

    private lateinit var binding: FragmentTopHeadlinesBinding
    private lateinit var adapter: TopHeadlinesAdapter
    private val allowedAuthenticators = BIOMETRIC_WEAK
    private var dialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopHeadlinesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.state = viewModel.state
        adapter = TopHeadlinesAdapter(viewModel::onArticleClick)
        binding.rvTopHeadlines.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservables(viewModel.state)
        setupRoutes(viewModel.state)

        if (savedInstanceState == null) {
            viewModel.getTopHeadlines()

            if (BuildConfig.ENABLE_FINGERPRINT_SCANNING &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                isBiometricAuthAvailable()) {
                showBiometricPrompt()
            }
        }
    }

    private fun setupObservables(state: TopHeadlinesScreenState) {
        state.errorMessage.observe(viewLifecycleOwner) { message ->
            this.showErrorDialog(requireContext(), message)
        }

        state.articlePage.observe(viewLifecycleOwner) { page ->
            lifecycleScope.launch {
                adapter.submitData(page)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { state ->
                when(state.refresh) {
                    is LoadState.NotLoading -> binding.cpiFetching.visibility = View.GONE
                    LoadState.Loading -> binding.cpiFetching.visibility = View.VISIBLE
                    is LoadState.Error -> showErrorDialog(
                        requireContext(),
                        (state.refresh as LoadState.Error).error.toString()
                    )
                }
            }
        }
    }

    private fun setupRoutes(state: TopHeadlinesScreenState) {
        state.articleProfileRoute.observe(viewLifecycleOwner) { article->
            ArticleContentActivity.open(requireContext(), article)
        }
    }

    private fun isBiometricAuthAvailable(): Boolean {
        val manager = BiometricManager.from(requireContext())
        return manager.canAuthenticate(allowedAuthenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun showBiometricPrompt() {
        val context = requireContext()
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val msg = getString(R.string.biometric_login_auth_error_message, errString, errorCode.toString())
                    showErrorDialog(context, msg)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    val msg = getString(R.string.biometric_login_auth_failed_message)
                    showErrorDialog(context, msg)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_login_dialog_title))
            .setSubtitle(getString(R.string.biometric_login_dialog_subtitle))
            .setAllowedAuthenticators(allowedAuthenticators)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showErrorDialog(ctx: Context, message: String) {
        dialog = AlertDialog.Builder(ctx)
            .setTitle(R.string.error_dialog_title)
            .setMessage(message)
            .setPositiveButton(R.string.error_dialog_ok_button) { _, _ ->
                requireActivity().finish()
            }
            .setCancelable(false)
            .create()
        dialog?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}


