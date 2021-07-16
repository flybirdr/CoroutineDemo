package me.rookie.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class WithcontextActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withcontext_demo)
        withContext()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    fun withContext() {
        Log.e("WithcontextActivity", ">>>>before launch coroutine")

        launch {
            Log.e("WithcontextActivity", "start launched on ${Thread.currentThread().name}")

            withContext(Dispatchers.IO) {
                Log.e("WithcontextActivity", "withContext on ${Thread.currentThread().name}")
            }

            //直接捕获withContext
            try {
                withContext(Dispatchers.Default) {
                    throw RuntimeException("exception in withContext 1")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //在withContext中捕获
            withContext(Dispatchers.Default) {
                try {
                    throw RuntimeException("exception in withContext 2")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //捕获suspend函数
            try {
                withContextFun()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Log.e("WithcontextActivity", ">>>>after launch coroutine")
    }

    suspend fun withContextFun(): Nothing = withContext(Dispatchers.IO) {
        Log.e("WithcontextActivity", "suspend on ${Thread.currentThread().name}")
        throw RuntimeException("exception in suspend withContext 3")
    }
}