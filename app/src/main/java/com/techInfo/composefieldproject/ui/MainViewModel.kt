package com.imkhalid.composefieldproject.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.imkhalid.composefieldproject.composeField.ComposeSections
import com.imkhalid.composefieldproject.model.CustomFormData
import com.imkhalid.composefieldproject.model.LoadingModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application)  {
    val sections :ComposeSections by lazy {
        ComposeSections()
    }
    data class SectionState(
        val loadingModel: LoadingModel = LoadingModel(),
        var section:CustomFormData?=null,
    )

    private val _sectionState = MutableStateFlow(SectionState())
    val sectionState = _sectionState.asStateFlow()




     fun getSections(){
        _sectionState.update {
            it.copy(
                loadingModel = it.loadingModel.copy(
                    isLoading = true,
                    shouldCallApi = false
                )
            )
        }
         viewModelScope.launch(Dispatchers.IO) {
             val client = OkHttpClient()
             val mediaType: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
             val json = JSONObject().toString()

             val url = HttpUrl.Builder()
                 .scheme("https")
                 .host("stagingenv.jubileelife.com")
                 .addPathSegment("api")
                 .addPathSegment("customer")
                 .addPathSegment("quote_fields")
                 .addQueryParameter("stage", "3")
                 .addQueryParameter("plan_id", "76")
                 .addQueryParameter("customer_quotation_id", "4166")
                 .addQueryParameter("subclass_id", "6")
                 .build()

             Log.d("TAG", "getSections: $url")
             val request: Request = Request.Builder()
                 .url(url) // Replace with your URL
                 .get() // Use the body you've created above
                 .addHeader(
                     "Authorization",
                     "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3N0YWdpbmdlbnYuanViaWxlZWxpZmUuY29tL2FwaS9jdXN0b21lci9sb2dpbiIsImlhdCI6MTcxMjIxOTc2NCwiZXhwIjoxNzEyMjI2OTY0LCJuYmYiOjE3MTIyMTk3NjQsImp0aSI6Ilh0Z1hVY0VCTGVMUzhJTFUiLCJzdWIiOjE4MDUsInBydiI6IjYyNDU3NTM5YTc3YjY3MDUyOWZiZDY3NTNmMGIzMTE0NGE5YTY3M2UifQ.woXaLh0tIyWKp7T7uxEcR3hid2Dz-Lm0QQ35BtCvpzg"
                 )
                 .addHeader("distribution","d2c")
                 .build()

             try {
                 val response =
                     async { client.newCall(request).execute() }.await() // Synchronous call
                 // Use the response
                 val responseData = response.body!!.string()
                 Log.d("TAG", "getSections: $responseData")
                 val resp = Gson().fromJson(responseData, CustomFormData::class.java)

                 viewModelScope.launch(Dispatchers.Main) {
                     _sectionState.update {
                         it.copy(
                             section = resp,
                             loadingModel = it.loadingModel.copy(
                                 isLoading = false
                             )
                         )
                     }
                 }

             } catch (e: Exception) {
                 viewModelScope.launch(Dispatchers.Main) {
                     _sectionState.update {
                         it.copy(
                             loadingModel = it.loadingModel.copy(
                                 isLoading = false,
                                 error = true,
                                 errors = e.message ?: ""
                             )
                         )
                     }
                 }
                 e.printStackTrace()
             }
        }
    }
}