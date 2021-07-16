package me.rookie.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*

class CoroutineConcurrentActivity : AppCompatActivity() ,CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_concurrent)
        CoroutineConcurrentDemo()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
    fun CoroutineConcurrentDemo() {
        Log.e("CoroutineConcurrent", ">>>>before launch coroutine")

        launch {
            Log.e("CoroutineConcurrent", "start launched on ${Thread.currentThread().name}")
            repeat(1000){
                async(Dispatchers.IO) {
                    Log.e("CoroutineConcurrent", "async on ${Thread.currentThread().name} $it")
                    "return from async $it"
                }
            }
        }

        Log.e("CoroutineConcurrent", ">>>>after launch coroutine")

    }

}