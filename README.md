# ![Logo](src/main/res/drawable-mdpi/ic_launcher.png) Tuner [![Build Status](https://travis-ci.org/billthefarmer/tuner.svg?branch=master)](https://travis-ci.org/billthefarmer/tuner) [![Release](https://img.shields.io/github/release/billthefarmer/tuner.svg?logo=github)](https://github.com/billthefarmer/tuner/releases) [![Available on F-Droid](https://f-droid.org/wiki/images/c/ca/F-Droid-button_available-on_smaller.png)](https://f-droid.org/packages/org.billthefarmer.tuner)

An android accordion tuner with strobe and multiple notes. The
Windows, Mac and the Android version of this project are also on
[here](https://github.com/billthefarmer/ctuner
"https://github.com/billthefarmer/ctuner"). The app is available on
[F-Droid](https://f-droid.org/packages/org.billthefarmer.tuner)
and [here](https://github.com/billthefarmer/tuner/releases). See the
[Wiki](https://github.com/billthefarmer/tuner/wiki) or the app help
screen for detailed documentation.

![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Tuner-portrait.png)
&nbsp;
![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Tuner-settings.png)

 * 32 temperaments including Equal, Just, Pythagorean, Meantone
 * Filter options, including low pass, fundamental, note and octave filter
 * Optional strobe or musical staff display
 * Custom temperaments option
 * Transposition option
 * Solfège (DoRéMi) option

## Using

Hold the phone/tablet near the instrument, play a note, and observe the
display. In portrait, the app shows a scope with the input signal, a
spectrum showing the processed signal, the main display with the note
value, cents error, frequency error etc, a strobe display, and a meter
display showing the cents error. In landscape, the app shows just the
main display and the meter. There is a signal indicator in the action
bar which is useful in landscape mode.

## Building

You can either import the app into Android Studio or use standalone Gradle.
Don't use installed gradle unless it's the same version as here (see
`./gradle/wrapper/gradle-wrapper.properties`).

```shell
$ ./gradlew [assembleDebug|assemble|build]
```
