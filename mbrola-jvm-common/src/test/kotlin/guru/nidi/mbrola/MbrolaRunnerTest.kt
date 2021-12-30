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

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class MbrolaRunnerTest {
    @Test
    fun simpleRun() {
        val out = File("target/out.wav")
        out.delete()
        MbrolaRunner().run(
            Voice.fromClasspath("nl2/nl2").file,
            File("src/test/resources/simple.pho"),
            out
        )
        assertTrue(out.length() > 0)
    }

    @Test
    fun errorRun() {
        assertThrows(MbrolaExecutionException::class.java) {
            MbrolaRunner().run(
                File("../mbrola-jvm-voices/src/main/resources/nl2/nl2"),
                File("src/test/resources/error.pho"),
                File("target/out.wav")
            )
        }
    }
}
