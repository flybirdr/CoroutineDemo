package me.rookie.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FlowActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow)
        produceAndConsume()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    //flow称为冷流，只有在collect后才发射数据
    fun produceAndConsume() {
        launch {
            var signal = 0
            val f = flow {
                Log.e("flow", "emit on ${Thread.currentThread().name}")
                emit(signal++)
            }
                .flowOn(Dispatchers.IO)//指定了flow{}的调度器
                .map {
                    Log.e("flow", "map on ${Thread.currentThread().name}")
                    it.toFloat()
                }//map的调度器取决于 订阅协程所在的调度器

            //在Dispatchers.IO订阅
            val consumer1 = launch(Dispatchers.IO) {
                while (true) {
                    f.collect {
                        Log.e("consumer1", "collect on ${Thread.currentThread().name}")
                        Log.e("consumer1", "recevive $it")
                    }
                    delay(2000)
                }
            }
            //在Dispatchers.Main订阅
            val consumer2 = launch(Dispatchers.Main) {
                while (true) {
                    f.collect {
                        Log.e("consumer2", "collect on ${Thread.currentThread().name}")
                        Log.e("consumer2", "recevive $it")
                    }
                    delay(2000)
                }
            }

            consumer1.join()
            consumer2.join()

        }
    }
}