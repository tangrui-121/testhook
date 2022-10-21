package com.person.coroutines

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hua.testhook.R
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class CActivity : AppCompatActivity() {

    private lateinit var start: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start = findViewById(R.id.start)
        start.setOnClickListener { start() }

        val threadPoolExecutor = ThreadPoolExecutor(
            3, 5, 1, TimeUnit.SECONDS,
            LinkedBlockingQueue<Runnable>(100)
        )
        threadPoolExecutor.execute {
            try {
                //处理相应事务
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        lifecycleScope.launchWhenResumed {

        }

        // ac和fragment的life协程要分开 因为生命周期不一样 一起混用还会内存泄漏的风险
//        val dialog = Dialog(this@MainActivity)
//        dialog.show()
//        (dialog.context as LifecycleOwner).lifecycleScope.requestMain {
//            withContext(Dispatchers.IO){
//                //网络加载
//            }
//            // 刷新UI
//        }
//        dialog.cancel()

        test3()

    }

    private fun start() {
//        val runBlockingJob = runBlocking {
//            Log.d("协程协程协程", "runBlocking 启动一个协程")
//            41
//        }
//        Log.d("协程协程协程", "runBlocking = $runBlockingJob")
//        val launchJob = GlobalScope.launch{
//            Log.d("协程协程协程", "launch 启动一个协程")
//        }
//        Log.d("协程协程协程", "launch = $launchJob")
//        val asyncJob = GlobalScope.async{
//            Log.d("协程协程协程", "async 启动一个协程")
//            "我是返回值"
//        }
//        Log.d("协程协程协程", "async = $asyncJob")

        GlobalScope.launch(Dispatchers.Main) {
            for (index in 1 until 100) {
                launch {
                    Log.d("协程协程协程", "launch$index")
                }
            }
        }
    }

    private fun testCoroutineContext() {
        val coroutineContext1 = Job() + CoroutineName("这是第一个上下文")
        Log.d("coroutineContext1", "$coroutineContext1")
        val coroutineContext2 = coroutineContext1 + Dispatchers.Default + CoroutineName("这是第二个上下文")
        Log.d("coroutineContext2", "$coroutineContext2")
        val coroutineContext3 = coroutineContext2 + Dispatchers.Main + CoroutineName("这是第三个上下文")
        Log.d("coroutineContext3", "$coroutineContext3")
    }

    private fun testCoroutineStart() {
        // 立即调度 但不是立即执行  所以下面直接cancel log可能不打
        val defaultJob = GlobalScope.launch {
            Log.d("defaultJob", "CoroutineStart.DEFAULT")
        }
        defaultJob.cancel()
        //
        val lazyJob = GlobalScope.launch(start = CoroutineStart.LAZY) {
            Log.d("lazyJob", "CoroutineStart.LAZY")
        }
        val atomicJob = GlobalScope.launch(start = CoroutineStart.ATOMIC) {
            Log.d("atomicJob", "CoroutineStart.ATOMIC挂起前")
            delay(100)
            Log.d("atomicJob", "CoroutineStart.ATOMIC挂起后")
        }
        atomicJob.cancel()
        val undispatchedJob = GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
            Log.d("undispatchedJob", "CoroutineStart.UNDISPATCHED挂起前")
            delay(100)
            Log.d("undispatchedJob", "CoroutineStart.UNDISPATCHED挂起后")
        }
        undispatchedJob.cancel()
    }

    private fun testException(){
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.d("exceptionHandler", "${coroutineContext[CoroutineName].toString()} 处理异常 ：$throwable")
        }
        val supervisorScope = CoroutineScope(SupervisorJob() + exceptionHandler)
        with(supervisorScope) {
            launch{
            }
            //省略...
        }

//        intent.getParcelableExtra<A>("")

        lifecycleScope.launch {
            flow {
                for (i in 1..3) {
                    delay(100)
                    emit(i)
                }
            }.collect { value -> Log.d("", "value :${value}") }
        }

    }


    private fun simple(): Sequence<Int> = sequence {
        for (i in 1..3) {
            Thread.sleep(100)
            yield(i)
        }
    }

    fun test() {
        simple().forEach { value ->
            Log.d("wwwww", "value :${value}")
        }
    }

    fun test1() {
        lifecycleScope.launch {
            flow {
                for (i in 1..3) {
                    Log.d("", "flow :${ currentCoroutineContext()}")
                    delay(100)
                    emit(i)
                }
            }.flowOn(Dispatchers.IO)
                .map {
                    Log.d("", "map :${ currentCoroutineContext()}")
                    it
                }.flowOn(Dispatchers.Default)
                .collect { value ->
                    Log.d("", "collect:${ currentCoroutineContext()} value :${value}")
                }
        }
    }

    fun test3() {
        lifecycleScope.launch {
            flow {
                Log.d("11111", "flow")
                for (i in 1..3) {
                    Log.d("11111", "flow")
                    delay(100)
                    emit(i)
                }
            }.onStart {
                Log.d("11111", "onStart ")
            }.onEach {
                Log.d("11111", "onEach :${it}")
            }.onCompletion {
                Log.d("11111", "onCompletion")
            }.collect { value ->
                Log.d("11111", "collect :${value}")
            }
        }

        repeat(Int.MAX_VALUE) {

        }


    }

    fun test4(){
        runBlocking {
            val squares = produceTest()
            squares.consumeEach { println("receive ：$it") }
            println("Done!")
        }
    }

    private fun CoroutineScope.produceTest(): ReceiveChannel<Int> = produce {
        for (x in 1..5) send(x)
    }
}

