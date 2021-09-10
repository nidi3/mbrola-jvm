/*
 * Copyright © 2018 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.mbrola

import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

object Runner {
    val work = File(System.getProperty("java.io.tmpdir"), "mbrola-jvm")

    init {
        work.mkdirs()
    }

    fun run(vararg args: String): Waveform {
        val os = System.getProperty("os.name").lowercase()
        return when {
            os.contains("win") -> doRun("mbrola.exe", *args)
            os.contains("mac") -> doRun("mbrola", *args)
            os.contains("linux") -> doRun("mbrola-linux-i386", *args)
            else -> throw IllegalStateException("Unsupported operating system $os")
        }
    }

    private fun doRun(name: String, vararg args: String): Waveform {
        val exec = File(work, name)
        if (!exec.exists()) {
            Thread.currentThread().contextClassLoader.getResourceAsStream(name).use { from ->
                if (from == null) throw IllegalStateException("mbrola implementation $name not found. Make sure you have the corresponding module in the classpath.")
                FileOutputStream(exec).use { to ->
                    from.copyTo(to)
                }
            }
            exec.setExecutable(true)
        }
        val proc = ProcessBuilder().command(exec.absolutePath, *args).start()
        val ok = proc.waitFor(30, TimeUnit.SECONDS)
        val output = File(args.last())
        if (!ok || (proc.exitValue() != 0 && (!output.exists() || output.length() == 0L)))
            throw MbrolaExecutionException("Executed " + exec.absolutePath + " " + Arrays.toString(args) + "\nResult: " + proc.exitValue().toString() + ": " + proc.inputStream.reader().readText() + proc.errorStream.reader().readText())
        return Waveform(File(args.last()))
    }
}

class MbrolaExecutionException(msg: String) : RuntimeException(msg)
