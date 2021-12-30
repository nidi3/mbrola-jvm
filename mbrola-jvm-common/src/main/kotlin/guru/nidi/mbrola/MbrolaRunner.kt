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

import java.io.*
import java.util.concurrent.TimeUnit

class MbrolaRunner(private val dockerContainer: String = "europe-west6-docker.pkg.dev/swiss-wowbagger/docker/mbrola") {
    private var native = true

    companion object {
        internal val work = File(System.getProperty("java.io.tmpdir"), "mbrola-jvm")

        init {
            work.mkdirs()
        }
    }

    fun run(voice: File, input: File, output: File, vararg args: String): File {
        return if (native) runNative(voice, input, output, *args)
        else runDocker(voice, input, output, *args)
    }

    fun runNative(voice: File, input: File, output: File, vararg args: String): File {
        val os = System.getProperty("os.name").lowercase()

        val executableFileName = when {
            os.contains("win") -> "mbrola.exe"
            os.contains("mac") -> "mbrola"
            os.contains("linux") -> "mbrola-linux-i386"
            else -> "mbrola"
        }

        var exec = File(work, executableFileName)
        if (!exec.exists()) {
            Thread.currentThread().contextClassLoader.getResourceAsStream(executableFileName).use { from ->
                if (from == null) {
                    // Use mbrola installed in operating system
                    exec = File("mbrola")
                } else {
                    FileOutputStream(exec).use { to -> from.copyTo(to) }
                }
            }
            exec.setExecutable(true)
        }
        return try {
            execute(
                output,
                exec.path,
                *args,
                voice.canonicalPath,
                input.canonicalPath,
                output.canonicalPath
            )
        } catch (e: IOException) {
            println("Could not run native mbrola: '${e.message}', using docker")
            native = false
            run(voice, input, output, *args)
        }
    }

    fun runDocker(voice: File, input: File, output: File, vararg args: String): File {
        return execute(
            output,
            "docker",
            "run",
            "-v", "${voice.parentFile.canonicalPath}:/tmp/voice",
            "-v", "${input.parentFile.canonicalPath}:/tmp/input",
            "-v", "${output.parentFile.canonicalPath}:/tmp/output",
            dockerContainer,
            "mbrola",
            *args,
            "/tmp/voice/${voice.name}",
            "/tmp/input/${input.name}",
            "/tmp/output/${output.name}"
        )
    }

    private fun execute(output: File, vararg command: String): File {
        val proc = ProcessBuilder().command(*command).start()
        val ok = proc.waitFor(30, TimeUnit.SECONDS)
        if (!ok || (proc.exitValue() != 0 && (!output.exists() || output.length() <= 44L)))
            throw MbrolaExecutionException(
                """Executed ${command.contentToString()}
               Result: ${proc.exitValue()}: 
               ${proc.inputStream.reader().readText()}
               ${proc.errorStream.reader().readText()}
            """.trimIndent()
            )
        return output
    }
}

class MbrolaExecutionException(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)
