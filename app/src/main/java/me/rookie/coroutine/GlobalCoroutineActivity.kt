package me.rookie.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GlobalCoroutineActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_coroutine)
        globalCoroutineDemo()
    }


    fun globalCoroutineDemo() {
        Log.e("globalCoroutineDemo", ">>>>before launch global coroutine")
        GlobalScope.launch {
            delay(1000)
            Log.e("globalCoroutineDemo", "launched on ${Thread.currentThread().name}")
        }
        Log.e("globalCoroutineDemo", ">>>>after launch global coroutine")
    }
}