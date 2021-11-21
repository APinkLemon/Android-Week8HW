package com.example.sjtu_network

import com.example.sjtu_network.api.YoudaoBean
import com.example.sjtu_network.interceptor.TimeConsumeInterceptor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import okhttp3.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.io.IOException

class MainActivity : AppCompatActivity() {
    var requestBtn: Button? = null
    var showText: TextView? = null
    var editText: EditText? = null

    private val okhttpListener = object : EventListener() {
        override fun dnsStart(call: Call, domainName: String) {
            super.dnsStart(call, domainName)
            showText?.text = showText?.text.toString() + "\nDNS搜索: " + domainName
        }

        override fun responseBodyStart(call: Call) {
            super.responseBodyStart(call)
            showText?.text = "\n正在连接, 请稍等..."
        }
    }
    val client: OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener).build()

    val gson = GsonBuilder().create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestBtn = findViewById(R.id.send_request)
        showText = findViewById(R.id.show_text)
        editText = findViewById(R.id.content)

        requestBtn?.setOnClickListener {
            showText?.text = ""
            click()
        }
    }

    private fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client.newCall(request).enqueue(callback)
    }

    private fun click() {
        val word = editText!!.text.toString()
        val url = "http://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=$word"
        request(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showText?.text = e.message
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                val youdaoBean = gson.fromJson(bodyString, YoudaoBean::class.java)

                showText?.text = showText?.text.toString() + "\n\n\n 查询结果: ${youdaoBean.translateResult[0][0].tgt}"
            }
        })
    }
}