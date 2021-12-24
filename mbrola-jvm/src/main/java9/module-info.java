module mbrola.jvm {
    requires transitive mbrola.jvm.linux;
    requires transitive mbrola.jvm.macos;
    requires transitive mbrola.jvm.windows;

    requires kotlin.stdlib;
    requires kotlin.stdlib.jdk7;
    requires kotlin.stdlib.jdk8;
}
