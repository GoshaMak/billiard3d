package org.wocy

import kotlinx.coroutines.*

fun main() = runBlocking {
    launch(Dispatchers.Main) { // runBlocking уже предоставляет CoroutineScope
        delay(1000)
        println("pre")
    }
    println("after")
    // Неявный job.join() при завершении runBlocking
}
