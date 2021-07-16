package me.rookie.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * launch/async用于启动协程
         * withContext()用于切换调度器
         * suspend修饰的函数只能在协程内或者suspend函数内调用，指示此函数是挂起函数会发生挂起,但如果没有发生调度则与普通函数无异
         *
         * withContext()只能实现串行
         * launch/async可以实现并发（官方叫做“结构化并发”），区别是launch无返回值，async返回一个Derfer可以调用await()获取其返回值
         */

        /**
         * 异常方面
         * 在协程内部或者子协程中发生异常会取消所有协程（主协程会被中断，所有子协程也会被取消），这叫做异常的扩散
         *
         * 搭配监督作业可以使子协程自己处理异常，即使发生未处理异常，也不会影响其他协程
         *
         */


        globalCoroutineDemo()

        launchAndAsyncDemo()

        exceptionInParentCoroutine()

        exceptionInChildCoroutine()

    }

    fun getName(): String {
        return "张三"
    }

    suspend fun getAge(): Int = withContext(Dispatchers.IO) {
        File(getExternalFilesDir("test")!!.absolutePath + "/test.txt").outputStream()
            .write("afasd".toByteArray())
        30
    }


    fun globalCoroutineDemo() {
        Log.e("globalCoroutineDemo", ">>>>before launch global coroutine")
        GlobalScope.launch {
            delay(5000)
            Log.e("globalCoroutineDemo", "launched on ${Thread.currentThread().name}")
        }
        Log.e("globalCoroutineDemo", ">>>>after launch global coroutine")
    }

    fun launchAndAsyncDemo() {
        Log.e("launchAndAsyncDemo", ">>>>before launch coroutine")

        launch {
            Log.e("launchAndAsyncDemo", "start launched on ${Thread.currentThread().name} default")
            val result = async {
                delay(500)
                Log.e("launchAndAsyncDemo", "async on ${Thread.currentThread().name} default")
                "return from async default"
            }
            Log.e(
                "launchAndAsyncDemo",
                "async completed on ${Thread.currentThread().name} with message ${result.await()}"
            )
        }

        launch(Dispatchers.IO) {
            Log.e("launchAndAsyncDemo", "start launched on ${Thread.currentThread().name}")
            val result = async {
                delay(500)
                Log.e("launchAndAsyncDemo", "async on ${Thread.currentThread().name}")
                "return from async"
            }
            Log.e(
                "launchAndAsyncDemo",
                "async completed on ${Thread.currentThread().name} with message ${result.await()}"
            )
        }

        Log.e("launchAndAsyncDemo", ">>>>after launch coroutine")

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

            //①try-catch,未try未设置CoroutineExceptionHandler会导致崩溃
//            try {
//
//                throw java.lang.RuntimeException("excetion in parent coroutine")
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

            //②ExceptionHandler,设置了CoroutineExceptionHandler会捕获异常，但会取消所有子协程
            throw java.lang.RuntimeException("excetion in parent coroutine")


            Log.e("exceptionInParent", ">>>>after launch coroutine")

        }


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

        launch() {

            launch(SupervisorJob() + handler) {
                //①try-catch,未try会导致异常传递到父协程，导致取消所有子协程
//            try {

                throw java.lang.RuntimeException("excetion in child coroutine")
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

                //②ExceptionHandler,设置了CoroutineExceptionHandler会捕获异常，但会取消所有子协程
//                throw java.lang.RuntimeException("excetion in parent coroutine")
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

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}