package me.rookie.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class CancelActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    /**
     * 配合decompile查看对应java代码发现kotlin对循环的处理极为灵活
     *
     * 对跳出循环的代码会进行相应的优化
     */

    /**
     * delay: 最终会调用scheduleResumeAfterDelay(),这个方法是Delay接口的抽象方法， 对于HandlerDispatcher，它的实现就是：
     * handler.postDelayed(block, timeMillis.coerceAtMost(MAX_DELAY))
     * continuation.invokeOnCancellation { handler.removeCallbacks(block) }
     * 来实现了delay的延时和取消功能
     *
     * 其中continuation来自suspendCancellableCoroutine
     * suspendCancellableCoroutine挂起当前协程，并且提供一个方法invokeOnCancellation()。直到手动调用resume...或者cancel,
     * cancel被调用时会回调invokeOnCancellation()，使我们能对取消操作做相应的处理。
     *
     * 应用场景的话，比如我们在一个协程中对一个文件或者临时数据库连接执行一连串的操作，在这中间可能取消这个协程，那么有了suspendCancellableCoroutine
     * 我们就可以在取消时关闭文件流或者数据库连接
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       val job= launch {
            var loopCount = 0
            while (true) {
                if (!isActive) {
                    Log.w("CancelActivity","canceled!")
                    break
                }
                Log.w("CancelActivity", "${++loopCount}")
                delay(3000L)
                Log.w("CancelActivity", "running")
            }
           Log.w("CancelActivity","exit loop!")
        }
        launch {
            delay(10000L)
            Log.w("CancelActivity","cancel job in loop")
            job.cancel()
        }

    }
}