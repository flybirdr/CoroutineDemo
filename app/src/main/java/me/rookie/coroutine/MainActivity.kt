package me.rookie.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        GlobalScope.launch {
//            delay(5000)
//            Log.e("GlobalScope", "launched on ${Thread.currentThread().name}")
//
//        }

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e(
                "MainScope",
                "handle coroutine exception on handler 0"
            )
            throwable.printStackTrace()
        }

        MainScope().launch(handler) {
            //on main thread
            Log.e("MainScope", "start launched on ${Thread.currentThread().name}")

            //swith dispatcher to io
            withContext(Dispatchers.IO) {
                Log.e("MainScope", "withContext IO launched on ${Thread.currentThread().name}")
            }

            //catch exception on io dispatcher
            withContext(Dispatchers.IO) {
                try {
                    Log.e(
                        "MainScope",
                        "catch exception withContext IO launched on ${Thread.currentThread().name}"
                    )
                    throw RuntimeException("exception on child coroutine")
                } catch (e: Exception) {

                }

            }

            //exception on io dispatcher 会中断协程执行
//            withContext(Dispatchers.IO) {
//                Log.e(
//                    "MainScope",
//                    "exception withContext IO launched on ${Thread.currentThread().name}"
//                )
//                throw RuntimeException("exception on child coroutine")
//
//            }

            val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
                Log.e(
                    "MainScope",
                    "handle coroutine exception on handler 1"
                )
                throwable.printStackTrace()
            }
            //exception on new coroutine
            val launchJob = MainScope().launch(SupervisorJob() + handler) {
                withContext(Dispatchers.IO) {
//                    delay(1000)
                    Log.e(
                        "MainScope",
                        "exception launch withContext IO launched on ${Thread.currentThread().name}"
                    )
                    throw RuntimeException("exception on child coroutine")
                }
            }
            //cancel
//            launchJob.cancel("cancel")

            val name = async(Dispatchers.IO, start = CoroutineStart.LAZY) {
                Log.e("MainScope", "getName on ${Thread.currentThread().name}")

                getName()
            }
            val age = async {
                Log.e("MainScope", "getAge on ${Thread.currentThread().name}")
                getAge()
            }
            Log.e("MainScope", "Async started")

            Log.e("MainScope", "Got persion info:  age[${age.await()}]")
            Log.e("MainScope", "Got persion info: name[${name.await()}]")



            Log.e("MainScope", "end launched on ${Thread.currentThread().name}")
        }
    }

    fun getName(): String {
        return "张三"
    }

    suspend fun getAge(): Int {
        return 30
    }
}