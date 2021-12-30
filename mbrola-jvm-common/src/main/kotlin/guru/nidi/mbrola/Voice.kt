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

class Voice private constructor(internal val file: File) {
    companion object {
        @JvmStatic
        fun fromFile(file: File) = Voice(file)

        @JvmStatic
        fun fromClasspath(path: String) = path.split("/").let { parts ->
            val file = File(MbrolaRunner.work, "${parts.takeLast(2).joinToString("/")}")
            if (!file.exists()) {
                Thread.currentThread().contextClassLoader.getResourceAsStream(path).use { from ->
                    if (from == null) throw IllegalArgumentException("$path not found in classpath")
                    file.parentFile.mkdirs()
                    FileOutputStream(file).use { to -> from.copyTo(to) }
                }
            }
            Voice(file)
        }
    }
}
