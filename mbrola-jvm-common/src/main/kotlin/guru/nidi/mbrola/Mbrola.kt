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

class Mbrola(
    private val voice: Voice,
    private val frequency: Double? = null,
    private val frequencyRatio: Double? = null,
    private val volume: Double? = null,
    private val time: Double? = null,
    private val format: Format = Format.WAV,
    private val runner: MbrolaRunner = MbrolaRunner()
) {
    constructor(voice: Voice) : this(voice, null)

    fun frequency(frequency: Double) = Mbrola(voice, frequency, frequencyRatio, volume, time, format, runner)
    fun frequencyRatio(frequencyRatio: Double) = Mbrola(voice, frequency, frequencyRatio, volume, time, format, runner)
    fun volume(volume: Double) = Mbrola(voice, frequency, frequencyRatio, volume, time, format, runner)
    fun time(time: Double) = Mbrola(voice, frequency, frequencyRatio, volume, time, format, runner)
    fun format(format: Format) = Mbrola(voice, frequency, frequencyRatio, volume, time, format, runner)
    fun runner(runner: MbrolaRunner) = Mbrola(voice, frequency, frequencyRatio, volume, time, format, runner)

    fun run(input: Phonemes): Waveform = run(input) { Waveform(it) }

    fun <T> run(input: Phonemes, consumer: (File) -> T): T {
        val inFile = File.createTempFile("input", ".pho", MbrolaRunner.work).apply {
            writeText(input.toString(1.0, 0))
        }
        val outFile = File.createTempFile("output", "." + format.name.lowercase(), MbrolaRunner.work)
        return try {
            val args = listOfNotNull(
                frequency?.let { "-l $it" }, frequencyRatio?.let { "-f $it" },
                time?.let { "-t $it" }, volume?.let { "-v $it" },
            )
            consumer(runner.run(voice.file, inFile, outFile, *args.toTypedArray()))
        } finally {
            inFile.delete()
            outFile.delete()
        }
    }

}

enum class Format {
    RAW, AU, WAV, AIFF
}
