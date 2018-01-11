# mbrola-jvm
A JVM wrapper around [MBROLA](http://tcts.fpms.ac.be/synthesis/mbrola.html) diphone synthesizer.

- mbrola-jvm-linux, mbrola-hvm-macos, mbrola-jvm-windows contain the corresponding native MBROLA executables.
- mbrola-jvm combines the three platform specific modules.
- mbrola-jvm-voices contains all available voices for MBROLA.

## Usage
Add a dependency to a platform specific executable
```xml
<dependency>
    <groupId>guru.nidi</groupId>
    <artifactId>mbrola-jvm-macos</artifactId>
    <version>0.0.1</version>
</dependency> 
```
or to all platforms
```xml
<dependency>
    <groupId>guru.nidi</groupId>
    <artifactId>mbrola-jvm-macos</artifactId>
    <version>0.0.1</version>
</dependency> 
```

Add a dependency to the voices
```xml
<dependency>
    <groupId>guru.nidi</groupId>
    <artifactId>mbrola-jvm-voices</artifactId>
    <version>0.0.1</version>
</dependency> 
```
or copy the desired folder(s) from mbrola-jvm-voices `src/main/resources` directly into your project.

Use it and have fun:
```kotlin
fun sayOneInDutch() {
    Mbrola(Phonemes.fromString("e n"), Voice.fromClasspath("nl1/nl1")).run().use {
        it.play(true)
    }
}
```
