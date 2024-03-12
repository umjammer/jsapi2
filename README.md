[![Release](https://jitpack.io/v/umjammer/jsapi.svg)](https://jitpack.io/#umjammer/jsapi)
[![Java CI](https://github.com/umjammer/jsapi/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/jsapi/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/jsapi/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/jsapi/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)
[![Parent](https://img.shields.io/badge/Parent-vavi--speech2-pink)](https://github.com/umjammer/vavi-speech2)

# JSAPI2

mavenized JSR-113 fka JSAPI2

 * this JSAPI 2.0 implementation DOES NOT support the J2ME platform (like CLDC 1.0, MIDP 1.0)
 * this JSAPI version 2.2.0 (**CAUTION** from 2.2.0 are my original versions, modified from SUN's original)
   * since 2.2.0 support service loader mechanism.
     * `speech.properties` and `EngineManager#registerEngineListFactory` works, but no more needed
 * volume property is enabled

## Install

 * [maven](https://jitpack.io/#umjammer/jsapi)

## Usage

 * [vavi-speech2](https://github.com/umjammer/vavi-speech2)

## TODO

 * ~~clean up remaining running threads at exiting~~
 * jsapi2 queue tests are not stable
   * using many mocks, so it's difficult to determine which is wrong (mock or implement)   
 * jsapi2/jse in sphinx4.1
 * ~~backport jsapi2 javadoc using codavaj~~
 * demo
 * mac module is temporary unavailable (because NSSpeechSynthesizer is deprecated) use rococoa module in [vavi-speech2](https://github.com/umjammer/vavi-speech2)
 * ~~match mechanism~~

---

[Original](https://github.com/JVoiceXML/jsapi)

JSAPI is an independent implementation of the JSAPI 2 standard. It provides a basic framework that can be used for a JSAPI 2 compliant access to speech engines. Demo implementations support FreeTTS, Sphinx 4, Microsoft Speech API 5.4 and the Mac OSX speech synthesizer.

Note that the framework is still under development and needs more work to be really compliant to the standard.
