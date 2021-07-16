package me.rookie.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SupervisorJobActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supervisor_job)

        exceptionInSupervisorScopeCoroutine()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    private fun exceptionInSupervisorScopeCoroutine() {
        Log.e("SupervisorScope", ">>>>before launch coroutine")

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e(
                "SupervisorScope",
                "handle coroutine exception on handler"
            )
            throwable.printStackTrace()
        }
        launch {

            //设置了SupervisorJob()+CoroutineExceptionHandler的子协程自己处理异常，不会影响其他子协程
            launch(SupervisorJob() + handler) {
                throw RuntimeException("excetion in child coroutine")
                Log.e("SupervisorScope", "launch on ${Thread.currentThread().name}")
            }

            async {
                Log.e("SupervisorScope", "async on ${Thread.currentThread().name}")
            }

            Log.e("SupervisorScope", ">>>>after launch coroutine")


        }
    }

}