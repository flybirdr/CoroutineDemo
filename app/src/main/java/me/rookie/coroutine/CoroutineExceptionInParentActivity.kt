package me.rookie.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*

class CoroutineExceptionInParentActivity : AppCompatActivity(),CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_exception_in_parent)
        exceptionInParentCoroutine()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
    private fun exceptionInParentCoroutine() {
        Log.e("exceptionInParent", ">>>>before launch coroutine")

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e(
                "exceptionInParent",
                "handle coroutine exception on handler"
            )
            throwable.printStackTrace()
        }

        launch(handler) {
            launch {
                Log.e("exceptionInParent", "launch on ${Thread.currentThread().name}")
            }
            launch {
                delay(3000)
                Log.e(
                    "exceptionInParent",
                    "launch on ${Thread.currentThread().name} after delay 3000"
                )
            }
            async {
                Log.e("exceptionInParent", "async on ${Thread.currentThread().name}")
            }
            async {
                delay(3000)
                Log.e(
                    "exceptionInParent",
                    "async on ${Thread.currentThread().name} after delay 3000"
                )
            }

            ////①try-catch,未try未设置CoroutineExceptionHandler会导致崩溃
            /*try {
                throw java.lang.RuntimeException("excetion in parent coroutine")
            } catch (e: Exception) {
                e.printStackTrace()
            }*/

            //②ExceptionHandler,设置了CoroutineExceptionHandler会捕获异常，但会取消所有子协程
            throw java.lang.RuntimeException("excetion in parent coroutine")


            Log.e("exceptionInParent", ">>>>after launch coroutine")

        }


    }
}