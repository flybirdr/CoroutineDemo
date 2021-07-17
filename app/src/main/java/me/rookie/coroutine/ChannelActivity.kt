package me.rookie.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class ChannelActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        produceAndConsume()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    fun produceAndConsume() {
        launch {
            /**
             * Channel.RENDEZVOUS只发送一个数据，直到这个数据被接收，否则生产者会一直被挂起
             * Channel.CONFLATED保留最近一次发送的数据，如果没有被接收，这个数据总是被更新
             * Channel.UNLIMITED无限保留所有数据，类似于LinkedBlockQueue
             * Channel.BUFFERED使用默认容量的缓冲区
             * [capacity]使用自定义容量的缓冲区
             */
            val channel = Channel<Int>(Channel.RENDEZVOUS)
            var signal = 0
            val producer = launch {
                while (true) {
                    Log.e("Channel", "send $signal")
                    channel.send(signal++)
                    delay(500)
                }
            }

            val consumer = launch {
                while (true) {
                    val receive = channel.receive()
                    Log.e("Channel", "receive $receive")
                    delay(2000)
                }

            }

            producer.join()
            consumer.join()

        }
    }
}