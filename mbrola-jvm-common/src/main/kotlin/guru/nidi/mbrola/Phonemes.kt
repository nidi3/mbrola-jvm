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

class Phonemes(val phonemes: List<Phoneme>) {
    fun toString(speed: Double, pitch: Int) = phonemes.joinToString("\n") { it.toString(speed, pitch) }

    companion object {
        fun fromFile(file: File) {
            //TODO
        }

        fun fromString(phonemes: String): Phonemes {
            val phs = mutableListOf(Phoneme("_", 10, listOf(Pair(0, 100))))
            val parts = phonemes.split(Regex("\\s+")).filter { it.isNotEmpty() }
            var i = 0
            while (i < parts.size) {
                val s = i++
                while (i < parts.size && parts[i].length > 1 && parts[i][0] in '0'..'9' && parts[i][1] in '0'..'9') i++
                val (len, ps) = if ((i - s) % 2 != 0) Pair(100, s + 1) else Pair(parts[s + 1].toInt(), s + 2)
                val pitches = mutableListOf<Pair<Int, Int>>()
                for (j in ps until i step 2) {
                    pitches += Pair(parts[j].toInt(), parts[j + 1].toInt())
                }
                phs += Phoneme(parts[s], len, pitches)
            }
            return Phonemes(phs)
        }
    }
}

data class Phoneme(val phoneme: String, val len: Int, val pitches: List<Pair<Int, Int>>) {
    fun toString(speed: Double, pitch: Int) =
            phoneme + " " + (len * speed).toInt() + " " + pitches.joinToString(" ") {
                "${it.first} ${it.second + pitch}"
            }
}