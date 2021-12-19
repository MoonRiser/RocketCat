package com.example.rocketcat

import android.widget.TextView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

/**
 * @author WayneXie
 * @date 2021/6/24
 */
class Test1 {

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("coroutine error msg : ${throwable.message}")
    }

    val lifecycleScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined + exceptionHandler)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val test1 = Test1()
            test1.onBegin()
            runBlocking {
//                test1.onStart()
            }
            test1.lifecycleScope.launch {
//                test1.flowTest2()
            }

        }
    }


    suspend fun flowTest2() {

        listOf(1, 2, 3, 4).map { }

        flowOf(1, 2, 3)
            .onEach { println("each $it") }
            .onCompletion { println("done") }
            .collect {

            }

    }

    fun onBegin() {
        (1..100).asSequence().onEach {
            println(it)
        }

    }

    class Success<T : Person> constructor(var data: T)

    sealed class Person
    data class Student(val name: String, val age: Int, val score: Int) : Person()
    data class Teacher(val name: String, val age: Int) : Person()

    open class Animal
    class Dog : Animal()
    class Cat : Animal()

//    fun test2134() {
//        val cat = Cat()
//        val dog = Dog()
//        val person1 = Person(cat)
//        val person2 = Person(dog)
//        val person3 = Person<Animal>(cat)
//
//        person1.give(person2)
//        person1.give(person3)
//
//
//    }

//    class Person<T>(var pet: T) {
//
//        fun give(p: Person<in T>) {
//            p.pet = this.pet
//            val my: T = p.pet
//        }
//
//    }


    private lateinit var userTv: TextView


    fun onStart() {


        lifecycleScope.launch(Dispatchers.IO) {

            val name = fetchUserName()
            val age = fetchUserAge()
            val msg = "姓名：${name} 年龄：${age}"
            showUser(msg)

        }

        lifecycleScope.launch {

            val name: Deferred<String> = async {
                fetchUserName()
            }
            val age: Deferred<Int> = async {
                fetchUserAge()
            }
            userTv.text = "姓名：${name.await()} 年龄：${age.await()}"

        }


    }

    private suspend fun showUser(msg: String) =
        withContext(Dispatchers.Main.immediate) {
            userTv.text = msg
        }

    private suspend fun fetchUserName() =
        withContext(Dispatchers.IO) {
            delay(500)//模拟网络耗时
            "小明"

        }

    private suspend fun fetchUserAge() =
        withContext(Dispatchers.IO) {
            delay(800)//模拟网络耗时
            17
        }


}