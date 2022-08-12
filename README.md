# Stash

[![License](https://img.shields.io/badge/license-MIT-green.svg?style=flat-square&label=License)](https://github.com/TheMrMilchmann/Stash/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.themrmilchmann.stash/stash.svg?style=flat-square&label=Maven%20Central)](https://maven-badges.herokuapp.com/maven-central/io.github.themrmilchmann.stash/stash)
[![JavaDoc](https://img.shields.io/maven-central/v/io.github.themrmilchmann.stash/stash.svg?style=flat-square&label=JavaDoc&color=blue)](https://javadoc.io/doc/io.github.themrmilchmann.stash/stash)
![Java](https://img.shields.io/badge/Java-18-green.svg?style=flat-square&color=b07219&logo=Java)

Stash is a Java library that provides capabilities to properly store secrets in memory.


## Usage

TODO


## Building from source

### Setup

This project uses [Gradle's toolchain support](https://docs.gradle.org/7.5.1/userguide/toolchains.html)
to detect and select the JDKs required to run the build. Please refer to the
build scripts to find out which toolchains are requested.

An installed JDK 1.8 (or later) is required to use Gradle.

### Building

Once the setup is complete, invoke the respective Gradle tasks using the
following command on Unix/macOS:

    ./gradlew <tasks>

or the following command on Windows:

    gradlew <tasks>

Important Gradle tasks to remember are:
- `clean`                   - clean build results
- `build`                   - assemble and test the Java library
- `publishToMavenLocal`       - build and install all public artifacts to the
  local maven repository

Additionally `tasks` may be used to print a list of all available tasks.


## License

```
Copyright (c) 2022 Leon Linhart

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```