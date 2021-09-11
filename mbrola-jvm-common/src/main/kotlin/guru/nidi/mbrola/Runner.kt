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
import java.io.IOException
import java.util.concurrent.TimeUnit

object Runner {
    val work = File(System.getProperty("java.io.tmpdir"), "mbrola-jvm")

    init {
        work.mkdirs()
    }

    fun run(vararg args: String): Waveform {
        val os = System.getProperty("os.name").lowercase()

        val executableFileName = when {
            os.contains("win") -> "mbrola.exe"
            os.contains("mac") -> "mbrola"
            os.contains("linux") ->"mbrola-linux-i386"
            else -> throw IllegalStateException("Unsupported operating system $os")
        }

        return doRun(executableFileName, *args)
    }

    private fun doRun(name: String, vararg args: String): Waveform {
        var exec = File(work, name)
        if (!exec.exists()) {
            Thread.currentThread().contextClassLoader.getResourceAsStream(name).use { from ->
                if (from == null) {
                    // Use mbrola installed in operating system
                    exec = File("mbrola")
                } else {
                    FileOutputStream(exec).use { to ->
                        from.copyTo(to)
                    }
                }
            }
            exec.setExecutable(true)
        }
        try {
            val proc = ProcessBuilder().command(exec.path, *args).start()
            val ok = proc.waitFor(30, TimeUnit.SECONDS)
            val output = File(args.last())
            if (!ok || (proc.exitValue() != 0 && (!output.exists() || output.length() == 0L)))
                throw MbrolaExecutionException("Executed " + exec.path + " " + args.contentToString() + "\nResult: "
                        + proc.exitValue().toString() + ": " + proc.inputStream.reader().readText() + proc.errorStream.reader().readText())
            return Waveform(File(args.last()))
        } catch (e: IOException) {
            throw MbrolaExecutionException("Cannot run MBROLA. If you are not using the platform specific mbrola-jvm-* " +
                    "modules make sure the mbrola command is available in your OS executables path", e)
        }
    }
}

class MbrolaExecutionException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)
