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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

object MbrolaTest {
    @Test
    fun simpleRun() {
        val out = File("target/out.wav")
        out.delete()
        Runner.run("../mbrola-jvm-voices/src/main/resources/nl2/nl2", "src/test/resources/simple.pho", out.path).use {
            assertTrue(out.exists())
        }
        assertFalse(out.exists())
    }

    @Test
    fun errorRun() {
        assertThrows(MbrolaExecutionException::class.java) {
            Runner.run("../mbrola-jvm-voices/src/main/resources/nl2/nl2", "src/test/resources/error.pho", "target/out.wav")
        }
    }

    @Test
    fun argsRun() {
        Mbrola(Phonemes.fromString("e n"), Voice.fromClasspath("nl1/nl1")).time(1.5).run().use {
            it.play(true)
        }
    }
}