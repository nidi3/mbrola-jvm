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

data class Mbrola private constructor(
    val input: Phonemes, val output: File, val voice: Voice,
    val frequency: Double?, val frequencyRatio: Double?, val volume: Double?, val time: Double?
) {

    constructor(input: Phonemes, voice: Voice, format: Format = Format.WAV) :
            this(
                input, File.createTempFile("output", "." + format.name.lowercase(), Runner.work),
                voice, null, null, null, null
            )

    fun output(output: File) = Mbrola(input, output, voice, frequency, frequencyRatio, volume, time)
    fun frequency(frequency: Double) = Mbrola(input, output, voice, frequency, frequencyRatio, volume, time)
    fun frequencyRatio(frequencyRatio: Double) = Mbrola(input, output, voice, frequency, frequencyRatio, volume, time)
    fun volume(volume: Double) = Mbrola(input, output, voice, frequency, frequencyRatio, volume, time)
    fun time(time: Double) = Mbrola(input, output, voice, frequency, frequencyRatio, volume, time)

    fun run(runner: Runner = Runner()): Waveform {
        val inFile = File.createTempFile("input", ".pho", Runner.work).apply {
            writeText(input.toString(1.0, 0))
        }
        try {
            val args = listOfNotNull(
                frequency?.let { "-l $it" }, frequencyRatio?.let { "-f $it" },
                time?.let { "-t $it" }, volume?.let { "-v $it" },
            )
            return runner.run(voice.file(), inFile, output, *args.toTypedArray())
        } finally {
            inFile.delete()
        }
    }

}

enum class Format {
    RAW, AU, WAV, AIFF
}
