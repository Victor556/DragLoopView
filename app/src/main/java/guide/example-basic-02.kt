/*
 * Copyright 2016-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This file was automatically generated from coroutines-guide.md by Knit tool. Do not edit.
package guide.basic.example02

import kotlinx.coroutines.experimental.*

fun main(args: Array<String>) = run {
    runBlocking<Unit> {
        // start main coroutine
        launch(CommonPool) {
            // create new coroutine in common thread pool
            println("111 ${Thread.currentThread().name}")
            delay(1000L)
            println("World! ${Thread.currentThread().name}")
        }
        println("Hello,${Thread.currentThread().name}") // main coroutine continues while child is delayed
        delay(2000L) // non-blocking delay for 2 seconds to keep JVM alive
    }
    println("222 ${Thread.currentThread().name}")
}
