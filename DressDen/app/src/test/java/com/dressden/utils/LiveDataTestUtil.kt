package com.dressden.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * Helper utility for testing LiveData objects
 */
object LiveDataTestUtil {
    /**
     * Gets the value of a LiveData safely, waiting for it to have a value.
     * This avoids the need to use CountDownLatch or similar synchronization mechanisms in tests.
     *
     * @param time The maximum time to wait for the LiveData to emit a value
     * @param timeUnit The time unit of the time parameter
     * @param T The type of data held by the LiveData
     * @throws TimeoutException If no value is set within the specified timeout
     */
    @Throws(TimeoutException::class)
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        try {
            afterObserve.invoke()

            // Don't wait indefinitely if the LiveData is not set
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }

        } finally {
            this.removeObserver(observer)
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

    /**
     * Observes a LiveData until the next emission is received, or a specified timeout.
     *
     * @param time The maximum time to wait for the LiveData to emit a value
     * @param timeUnit The time unit of the time parameter
     * @param block The code block to execute once we have an updated value
     * @param T The type of data held by the LiveData
     */
    fun <T> LiveData<T>.observeForTesting(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        block: (T) -> Unit
    ) {
        val observer = Observer<T> { value ->
            block(value)
        }
        try {
            observeForever(observer)
            Thread.sleep(timeUnit.toMillis(time))
        } finally {
            removeObserver(observer)
        }
    }

    /**
     * Gets all values a LiveData emits for a given period.
     *
     * @param time The maximum time to wait and collect values
     * @param timeUnit The time unit of the time parameter
     * @param T The type of data held by the LiveData
     */
    fun <T> LiveData<T>.captureValues(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): List<T> {
        val values = mutableListOf<T>()
        val observer = Observer<T> { value ->
            values.add(value)
        }
        try {
            observeForever(observer)
            Thread.sleep(timeUnit.toMillis(time))
        } finally {
            removeObserver(observer)
        }
        return values
    }

    /**
     * Observes a LiveData until a condition is met, or a timeout occurs.
     *
     * @param time The maximum time to wait for the condition to be met
     * @param timeUnit The time unit of the time parameter
     * @param condition The condition to wait for
     * @param T The type of data held by the LiveData
     * @throws TimeoutException If the condition is not met within the specified timeout
     */
    @Throws(TimeoutException::class)
    fun <T> LiveData<T>.waitUntil(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        condition: (T) -> Boolean
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                if (condition(value)) {
                    data = value
                    latch.countDown()
                    this@waitUntil.removeObserver(this)
                }
            }
        }

        this.observeForever(observer)

        try {
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("Condition was not met within the timeout period.")
            }
        } finally {
            this.removeObserver(observer)
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}
