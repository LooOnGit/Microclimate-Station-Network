package com.example.loofarm.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentLoginBinding
import com.example.loofarm.model.ThingSpeakManager
import com.example.loofarm.ui.share_viewmodel.LooFarmViewModel

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private val viewModel: LooFarmViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        observeViewModels()
    }

    private fun initListeners() {
        binding?.apply {
            btnLogin.setOnClickListener {
                ThingSpeakManager.channelId = tvChannelId.text.toString().toLongOrNull() ?: 0L
                ThingSpeakManager.apiKey = tvApiKey.text.toString()
                viewModel.getThingSpeakData(
                    channelId = ThingSpeakManager.channelId,
                    apiKey = ThingSpeakManager.apiKey
                )
            }
        }
    }

    private fun observeViewModels() {
        // Observe feeds LiveData
        viewModel.thingSpeakResponse.observe(viewLifecycleOwner) { thingSpeakResponse ->
            Log.d(TAG, "observeViewModels() called with: thingSpeakResponse = $thingSpeakResponse")
            //Todo: Update flow
            if (thingSpeakResponse != null) findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }

        // Observe loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Show or hide progress bar
            Log.d(TAG, "observeViewModels() called with: isLoading = $isLoading")
            binding?.progressBar?.isVisible = isLoading
        }

        // Observe error LiveData
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Log.d(TAG, "observeViewModels() called with: errorMessage = $errorMessage")
            if (errorMessage != null) {
                // Show error message
                Toast.makeText(requireContext(), "Login fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val TAG by lazy { LoginFragment::class.java.simpleName }
    }
}
