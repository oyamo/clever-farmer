package com.oyasis.fruity.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.oyasis.fruity.R
import com.oyasis.fruity.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnclickListeners()
        navController = findNavController()
    }

    private fun setupOnclickListeners() {
        binding.loginBtn.setOnClickListener {
            navigatetoLogin()
        }

        binding.registerBtn.setOnClickListener {
            navigateToHome()
        }
    }

    private fun navigatetoLogin(){
        navController.navigate(R.id.action_registerFragment_to_loginFragment)
    }

    private fun navigateToHome() {
        navController.navigate(R.id.action_registerFragment_to_homeNavhost)
    }
}