package com.oyasis.fruity.service

import com.oyasis.fruity.data.dto.GPTRequest
import com.oyasis.fruity.data.dto.GPTResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface Api {
    @POST("completions")
    fun makePrediction(@Body request: GPTRequest?, @Header("Authorization") header: String?, @Header("Content-Type") contentType: String = "application/json"): Call<GPTResponse?>?

    companion object {
        const val BASE_URL = "https://api.openai.com/v1/"
    }
}