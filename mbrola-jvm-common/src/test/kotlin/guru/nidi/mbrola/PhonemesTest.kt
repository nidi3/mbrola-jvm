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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class PhonemesTest {
    @Test
    fun fromFile() {
        val phs = Phonemes.fromFile(File("src/test/resources/morgen.pho"))
        val expect = Phonemes.fromString("_ G 10 204 73 219 88 219 u 54 35 192 d 46 37 187 @ 30 13 142 m 10 135 29 104 95 89 O 78 R G 10 76 @ 32 130 n 36 _ 162 16 141")
        assertEquals(expect, phs)
    }
}
