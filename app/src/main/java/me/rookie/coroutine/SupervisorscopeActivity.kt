package me.rookie.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SupervisorscopeActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisorscope)
        exceptionInSupervisorscopeActivityCoroutine()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    private fun exceptionInSupervisorscopeActivityCoroutine() {
        Log.e("SupervisorscopeActivity", ">>>>before launch coroutine")

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e(
                "SupervisorscopeActivity",
                "handle coroutine exception on handler"
            )
            throwable.printStackTrace()
        }

        //设置顶层的CoroutineExceptionHandler
        launch(handler) {
            //子协程运行在supervisorScope他们之间的异常不会互相影响
            supervisorScope {
                //在这里发生异常不会影响到下面的其他子协程
                launch {
                    throw RuntimeException("excetion in child launch()")
                    Log.e("SupervisorscopeActivity", "launch on ${Thread.currentThread().name}")
                }
                //延迟3秒后打印
                launch {
                    delay(3000)
                    Log.e(
                        "SupervisorscopeActivity",
                        "launch on ${Thread.currentThread().name} after delay 3000"
                    )
                }
                //在这里发生异常不会影响到下面的其他子协程
                async {
                    throw RuntimeException("excetion in child async()")
                    Log.e("SupervisorscopeActivity", "async on ${Thread.currentThread().name}")
                }
                //延迟3秒后打印
                async {
                    delay(3000)
                    Log.e(
                        "SupervisorscopeActivity",
                        "async on ${Thread.currentThread().name} after delay 3000"
                    )
                }
                Log.e("SupervisorscopeActivity", ">>>>after launch coroutine")
            }
        }
    }
}