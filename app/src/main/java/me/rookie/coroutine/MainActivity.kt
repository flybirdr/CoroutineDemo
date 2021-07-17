package me.rookie.coroutine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //launch/async用于启动协程

        //withContext()用于切换调度器,在同一协程内串行执行

        //suspend修饰的函数只能在协程内或者suspend函数内调用，
        //指示此函数是挂起函数会发生挂起,但如果没有发生调度则与普通函数无异
        //所以suspend应该修饰会发生调度的函数

        //launch/async可以实现并发（官方叫做“结构化并发”）
        // 区别是launch无返回值，async返回一个Derfer可以调用await()获取其返回值

        //在父协程或者子协程中发生异常会取消所有协程（主协程会被中断，所有子协程也会被取消），这叫做异常的传播

        //Supervisor+CoroutineExceptionHandler可以使子协程自己处理异常，即使发生未处理异常，也不会影响其他协程

        //supervisorSocope{ }作用域内的协程的异常不会传播
    }

    fun GlobalCoroutine(view: View) {
        startActivity(Intent(this,GlobalCoroutineActivity::class.java))
    }
    fun CoroutineConcurrentActivity(view: View) {
        startActivity(Intent(this,CoroutineConcurrentActivity::class.java))
    }
    fun WithcontextActivity(view: View) {
        startActivity(Intent(this,WithcontextActivity::class.java))
    }
    fun CoroutineExceptionInParentActivity(view: View) {
        startActivity(Intent(this,CoroutineExceptionInParentActivity::class.java))
    }
    fun CoroutineExceptionInChildActivity(view: View) {
        startActivity(Intent(this,CoroutineExceptionInChildActivity::class.java))
    }
    fun SupervisorJobActivity(view: View) {
        startActivity(Intent(this,SupervisorJobActivity::class.java))
    }
    fun SupervisorscopeActivity(view: View) {
        startActivity(Intent(this,SupervisorscopeActivity::class.java))
    }

    fun FlowActivity(view: View) {
        startActivity(Intent(this,FlowActivity::class.java))
    }

    fun ChannelActivity(view: View) {
        startActivity(Intent(this,ChannelActivity::class.java))
    }


}