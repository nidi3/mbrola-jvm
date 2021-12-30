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
import javax.sound.sampled.*

class Waveform(file: File) {
    val bytes = file.readBytes().also { file.delete() }

    val stream get() = bytes.inputStream()

    fun playback() = play(false)

    fun playAndWait() = play(true)

    private fun play(waitUntilDone: Boolean) {
        AudioSystem.getAudioInputStream(stream).use { stream ->
            val listener = ClosingListener()
            AudioSystem.getClip().apply {
                addLineListener(listener)
                open(stream)
                use { clip ->
                    clip.start()
                    if (waitUntilDone) listener.waitUntilDone()
                }
            }
        }
    }
}

internal class ClosingListener : LineListener {
    private val obj = Object()
    private var done = false

    override fun update(event: LineEvent) {
        val type = event.type
        if (type == LineEvent.Type.STOP || type == LineEvent.Type.CLOSE) {
            done = true
            synchronized(obj) {
                obj.notifyAll()
            }
        }
    }

    fun waitUntilDone() {
        synchronized(obj) {
            while (!done) {
                obj.wait()
            }
        }
    }
}
