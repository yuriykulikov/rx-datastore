/*
 * MIT License
 *
 * Copyright (c) 2020 Yuriy Kulikov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.yuriykulikov.rxdatastore

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class FileDataStore<T : Any> private constructor(
    private val name: String,
    private val filer: Filer,
    private val adapter: JsonAdapter<T>,
    initial: T
) : RxDataStore<T> {
    private val subject: BehaviorSubject<T> = BehaviorSubject.createDefault(initial)

    override var value: T
        get() = requireNotNull(subject.value)
        set(value) {
            subject.onNext(value)
            filer.sink(name).use { sink ->
                adapter.toJson(sink, value)
            }
        }

    override fun observe(): Observable<T> {
        return subject.hide()
    }

    fun dump(logger: (String) -> Unit) {
        logger("Dump $name")
        adapter.toJson(value)
            .lines()
            .forEach {
                logger(it)
            }
    }

    companion object {
        fun <T> listDataStore(
            filer: Filer,
            name: String,
            clazz: Class<T>,
            defaultValue: String,
            moshi: Moshi
        ): FileDataStore<List<T>> {
            val adapter: JsonAdapter<List<T>> = moshi
                .adapter<List<T>>(Types.newParameterizedType(List::class.java, clazz))
                .indent("  ")

            val initial: List<T> = filer.source(name)
                ?.use {
                    runCatching {
                        adapter.fromJson(it)
                    }.getOrDefault(adapter.fromJson(defaultValue))
                }
                ?: requireNotNull(adapter.fromJson(defaultValue))

            return FileDataStore(name, filer, adapter, initial)
        }

        fun <T : Any> dataStore(
            filer: Filer,
            name: String,
            clazz: Class<T>,
            defaultValue: T,
            moshi: Moshi
        ): FileDataStore<T> {
            val adapter: JsonAdapter<T> = moshi
                .adapter(clazz)
                .indent("  ")

            val initial: T = filer.source(name)
                ?.use {
                    runCatching {
                        adapter.fromJson(it)
                    }.getOrDefault(defaultValue)
                }
                ?: defaultValue

            return FileDataStore(name, filer, adapter, initial)
        }
    }
}

inline fun <reified T : Any> FileDataStore.Companion.listDataStore(
    filer: Filer,
    name: String,
    defaultValue: String,
    moshi: Moshi
) = listDataStore(filer, name, T::class.java, defaultValue, moshi)

