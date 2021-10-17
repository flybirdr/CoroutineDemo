package me.rookie.coroutine

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume

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

    val mutableStateFlow = MutableStateFlow<Int>(0)
    val mutableSharedFlow = MutableSharedFlow<Int>()

    //热流
    fun hotFlow() {
        launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mutableStateFlow.collect {
                    Toast.makeText(this@FlowActivity, "StateFlow: $it", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun LifecycleOwner.repeatOnLifecycle(
        state: Lifecycle.State,
        block: suspend () -> Unit
    ) {
        lifecycle.repeatOnLifecycle(state, block)
    }

    suspend fun Lifecycle.repeatOnLifecycle(
        state: Lifecycle.State,
        block: suspend () -> Unit
    ) {
        require(state !== Lifecycle.State.INITIALIZED) {
            "repeatOnLifecycle cannot start work with the INITIALIZED lifecycle state."
        }

        if (currentState === androidx.lifecycle.Lifecycle.State.DESTROYED) {
            return
        }
        coroutineScope {
            withContext(Dispatchers.Main.immediate) {
                var job: Job? = null
                var observer: LifecycleObserver? = null
                suspendCancellableCoroutine<Unit> { ccont ->
                    val resumeEvent = state.toEvent()
                    val suspendEvent = state.downEvent()
                    observer = object : LifecycleEventObserver {
                        override fun onStateChanged(
                            source: LifecycleOwner,
                            event: Lifecycle.Event
                        ) {
                            if (event == resumeEvent) {
                                job = launch { block() }
                            } else if (event == suspendEvent) {
                                job?.cancel()
                            } else if (event == Lifecycle.Event.ON_DESTROY) {
                                ccont.resume(Unit)
                            }
                        }
                    }
                    addObserver(observer!!)
                }
                observer?.let {
                    removeObserver(it)
                }
                job?.cancel()
            }
        }
    }
}

private fun Lifecycle.State.downEvent() = when (this) {
    Lifecycle.State.CREATED ->
        Lifecycle.Event.ON_DESTROY
    Lifecycle.State.STARTED ->
        Lifecycle.Event.ON_STOP
    Lifecycle.State.RESUMED ->
        Lifecycle.Event.ON_PAUSE
    else -> throw IllegalArgumentException("illegale state")
}


private fun Lifecycle.State.toEvent() = when (this) {
    Lifecycle.State.CREATED ->
        Lifecycle.Event.ON_CREATE
    Lifecycle.State.STARTED ->
        Lifecycle.Event.ON_START
    Lifecycle.State.RESUMED ->
        Lifecycle.Event.ON_RESUME
    Lifecycle.State.DESTROYED ->
        Lifecycle.Event.ON_DESTROY
    else -> throw IllegalArgumentException("illegale state")
}
