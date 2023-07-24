package com.oyasis.fruity.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oyasis.fruity.R
import com.oyasis.fruity.common.Constants
import com.oyasis.fruity.data.dto.GPTRequest
import com.oyasis.fruity.data.dto.GPTResponse
import com.oyasis.fruity.databinding.FragmentInformationBinding
import com.oyasis.fruity.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"


class InformationFragment : BottomSheetDialogFragment() {
    private var diseaseParameter: String? = null
    private lateinit var binding: FragmentInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            diseaseParameter = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentInformationBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
                InformationFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.diseaseName.setText(diseaseParameter)
        getDetails()
    }


    private fun getDetails() {
        val gptCall: RetrofitClient? = RetrofitClient.getInstance() ?: return
        val api = gptCall!!.myApi

        val gptRequest = GPTRequest()
        gptRequest.temperature = 0
        gptRequest.prompt = "Signs, Symptoms, Prevention and cure for $diseaseParameter"
        gptRequest.maxTokens = 400
        gptRequest.model = "text-davinci-003"

        val call = api.makePrediction(gptRequest, Constants.OPEN_API_KEY)
        call?.enqueue(object : Callback<GPTResponse?>{
            override fun onResponse(call: Call<GPTResponse?>, response: Response<GPTResponse?>) {
                val gptResponse = response.body()
                gptResponse?.let {
                    val choices = it.choices
                    if(choices.isEmpty()) return@let
                    binding.diseaseDescription.text = choices[0].text
                }?: binding.diseaseDescription.setText("Could not find information: (GOT STATUS ${response.code()})")
            }

            override fun onFailure(call: Call<GPTResponse?>, t: Throwable) {
                binding.diseaseDescription.setText("Could not find information...")
            }

        })

    }
}