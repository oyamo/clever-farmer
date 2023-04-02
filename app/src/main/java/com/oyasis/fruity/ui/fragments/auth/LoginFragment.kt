package com.oyasis.fruity.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.oyasis.fruity.R
import com.oyasis.fruity.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setupOnclickListener()

    }


    private fun setupOnclickListener() {
        binding.registerBtn.setOnClickListener { navigateToRegistration() }
        binding.loginBtn.setOnClickListener { navigateToHome() }
    }


    private fun navigateToHome() {
        navController.navigate(R.id.action_loginFragment_to_homeNavhost)
    }

    private fun navigateToRegistration() {
        navController.navigate(R.id.action_loginFragment_to_registerFragment)
    }

}