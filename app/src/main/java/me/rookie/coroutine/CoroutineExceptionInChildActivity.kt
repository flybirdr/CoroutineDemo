package me.rookie.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class CoroutineExceptionInChildActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_exception_in_child)
        exceptionInChildCoroutine()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    private fun exceptionInChildCoroutine() {
        Log.e("exceptionInChild", ">>>>before launch coroutine")

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e(
                "exceptionInChild",
                "handle coroutine exception on handler"
            )
            throwable.printStackTrace()
        }

        launch(handler) {
            //父协程设置了CoroutineExceptionHandler会捕获异常，但会取消所有子协程
            launch {
                throw java.lang.RuntimeException("excetion in child coroutine")
                Log.e("exceptionInChild", "launch on ${Thread.currentThread().name}")
            }
            launch {
                delay(3000)
                Log.e(
                    "exceptionInChild",
                    "launch on ${Thread.currentThread().name} after delay 3000"
                )
            }
            async {
                Log.e("exceptionInChild", "async on ${Thread.currentThread().name}")
            }
            async {
                delay(3000)
                Log.e(
                    "exceptionInChild",
                    "async on ${Thread.currentThread().name} after delay 3000"
                )
            }

            Log.e("exceptionInChild", ">>>>after launch coroutine")

        }
    }
}