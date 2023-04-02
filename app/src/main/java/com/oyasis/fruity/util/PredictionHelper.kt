package com.oyasis.fruity.util

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import com.oyasis.fruity.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifierHelper(
    private val context: Context,
    private val imageClassifierListener: ClassifierListener?
) {
    private var imageClassifier: Model? = null

    init {
        setupImageClassifier()
    }

    fun clearImageClassifier() {
        imageClassifier?.close()
        imageClassifier = null
    }

    private fun setupImageClassifier() {
        try {
            imageClassifier = Model.newInstance(context)


        } catch (e: IOException) {
            Log.e(TAG, "Error initialosing: " + e.message)
        }
    }

    fun classify(image: Bitmap, rotation: Int) {
        if (imageClassifier == null) {
            setupImageClassifier()
        }


        // Inference time is the difference between the system time at the start and finish of the
        var inferenceTime = SystemClock.uptimeMillis()

        val tensorImage = TensorImage.fromBitmap(image) ?: return
        val outputs = imageClassifier!!.process(tensorImage)

        // Get the classification result from the output tensor buffer
        val outputFeature0 = outputs.probabilityAsCategoryList

        // update inference time
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        imageClassifierListener?.onResults(
            outputFeature0,
            inferenceTime
        )

        clearImageClassifier()
    }


    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Category>?,
            inferenceTime: Long
        )
        
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}