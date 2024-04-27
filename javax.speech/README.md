# JSAPI2 JSR-113

## Change Log

 * 2.0.6.0
   * jvoicexml final implementation
 * 2.0.6.3
   * doesn't support midp (cldc)
 * 2.1.0
   * JSR-113 final (maybe not reflected to this project?) 
 * 2.2.0
   * use java proper service loader mechanism
   * change version to use semantic versioning
 * 2.2.1
   * add voice comparison level for `SpeechLocal#match()` 

## Usage

### voice comparison level

* if you set level as `LENIENT` and a locale set for `EngineMode` like

```java
 System.setProperty("javax.speech.SpeechLocale.comparisonStrictness", "LENIENT");
 Synthesizer synthesizer = (Synthesizer) EngineManager.createEngine(new RococoaSynthesizerMode(new SpeechLocale("ja")));
 assert ((SynthesizerMode) synthesizer.getEngineMode()).getVoices().length > 0 : "will be succeed";
```

you can find voices which locale is 'ja' or 'ja_JP'.

* but is you set level as `STRICT` like

```java
 System.setProperty("javax.speech.SpeechLocale.comparisonStrictness", "STRICT");
 Synthesizer synthesizer = (Synthesizer) EngineManager.createEngine(new RococoaSynthesizerMode(new SpeechLocale("ja")));
 assert ((SynthesizerMode) synthesizer.getEngineMode()).getVoices().length > 0 : "will be failed";
```

you can find voices which locale is 'ja' only, voices locale 'ja_JP' are not included.

## TODO

 * i18n `SpeechLocal`