package com.me.kt_cook_book.ui.fragments.splash

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.me.kt_cook_book.R
import com.me.kt_cook_book.databinding.FragmentSplashBinding
import com.me.kt_cook_book.ui.MainActivity
import com.me.kt_cook_book.viewmodels.MainViewModel

class SplashFragment : Fragment(R.layout.fragment_splash){
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSplashBinding.bind(view)

        mainViewModel.setDisplayBottomNavFlow(false)

        (requireActivity() as MainActivity).apply {
            supportActionBar?.hide()
            actionBar?.hide()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (requireActivity() as MainActivity).window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                hide(WindowInsets.Type.navigationBars())
            }
        }

            Handler(Looper.myLooper()!!).postDelayed({
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToRecipesFragment())
            }, 3000)
        }


        override fun onDestroy() {
            _binding = null
            super.onDestroy()
        }

    }