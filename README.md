# ![Logo](src/main/res/drawable-mdpi/ic_launcher.png) Tuner ![.github/workflows/main.yml](https://github.com/billthefarmer/tuner/workflows/.github/workflows/main.yml/badge.svg) [![Release](https://img.shields.io/github/release/billthefarmer/tuner.svg?logo=github)](https://github.com/billthefarmer/tuner/releases)
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.svg" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/org.billthefarmer.tuner)

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

![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Tuner-landscape.png)

 * 32 temperaments including Equal, Just, Pythagorean, Meantone
 * Filter options, including low pass, fundamental, note and octave filter
 * Optional strobe or musical staff display
 * Custom temperaments option
 * Transposition option
 * Solfège (DoRéMi) option
 * Bach (ABHC) option

## Using

Hold the phone/tablet near the instrument, play a note, and observe
the display. In portrait, the app shows a scope with the input signal,
a spectrum showing the processed signal, the main display with the
note value, cents error, frequency error etc, a strobe display or
staff display and a meter display showing the cents error. In
landscape the app shows just the main display or staff and the
meter. There is a signal indicator in the action bar which is useful
in landscape mode. Using the touch setting changes below causes a
short notification message to pop up for a few seconds.

### Temperaments

The app defaults to **equal** temperament and the key of **C**. 32
temperaments are available in the settings. For all temperaments
except for equal it is necessary to choose a key.  Note that the
display may show a cents variation greater than 50 for some notes in
some temperament due to the interval between notes being greater than
100 cents. See [Musical
temperament](https://en.wikipedia.org/wiki/Musical_temperament).

### Custom Temperaments

You may load custom temperaments by placing a file in
`Tuner/Custom.txt`. The file should contain one or more entries
formatted as property entries. The format is documented
[here](https://developer.android.com/reference/java/util/Properties#load\(java.io.Reader\)).

```properties
# Custom Temperaments
Custom: 1.000000000, 1.067871094, 1.125000000, 1.185185185, \
        1.265625000, 1.333333333, 1.423828125, 1.500000000, \
        1.601806641, 1.687500000, 1.777777778, 1.898437500

# 41edo 12 note subset
41edo: 0.995933333333333, 1.07023430212233, 1.122462048, 1.17711684266417, \
       1.26504472893667, 1.3266973308906, 1.42571583230427, 1.49526051927677, \
       1.5680347591656, 1.68521247642303, 1.76730548352053, 1.8992638916125
```

Entries will be loaded and displayed in **Settings** in alphabetical
order at the end of the list. Erroneous entries will either be ignored
or replaced with ones (1.0). Changes to the contents of the custom
file will be loaded in **Settings**.

### Scope

Touch the scope to add an input low pass filter to make it easier to
tune bass reeds by removing harmonics. Touch again to remove it. Touch
and hold to add a fundamental filter. Touch and hold again to remove
it. The colours have been chosen both for clarity, and for that
'retro' look like a real piece of hardware.

### Spectrum

Touch the spectrum to zoom in to the current note. The display can
show up to eight reeds. Touch again to zoom back out. Touch and hold
to add a downsampling feature to help with bass reeds. This may
produce spurious results. Touch and hold again to remove.

### Display

Touch the display to lock the current results. Touch again to remove
the lock. Display lock is not preserved if the screen is
rotated. Touch and hold the display to switch between displaying the
closest result to the reference note and up to eight results. This
setting is not remembered the next time you use the app.

### Strobe

Touch the strobe to turn it on and off. The staff will display
instead. Strobe colours are configurable in the settings. Touch and
hold to change theme.

### Staff

Touch the staff to turn it on and off. The strobe will display instead.
Touch and hold to change theme. In landscape touch the staff to lock the
current results.

### Meter

The meter shows the cents error up to plus or minus 50 cents. Touch
the meter to copy the results to the clipboard. Touch and hold to
prevent the display turning itself off. Touch and hold again to allow
the display to turn off. In landscape touch the meter to switch between
the display and the staff.

### Status bar

The status bar shows the input sample rate and the status of the
various settings.

Settings
--------

All the settings are preserved except the multiple note option and
will be remembered the next time you use the app.

### Input

The **input source** may be changed. If a source that isn't present on
the device is selected an error message will appear. Select the
default source or the microphone.

#### Audio filter

The **audio filter** item controls a low pass filter in the audio
input before processing.

#### Downsample

The **downsample** item controls a downsampling feature on the audio
input during processing to help with bass reeds. This should be more
reliable now I have found the long standing deliberate mistake.

### Display

#### Keep the screen on

The **screen** item stops the screen turning itself off.

#### Spectrum zoom

The **spectrum zoom** item controls the zoom feature of the spectrum
display.

#### Display multiple notes

The **multiple notes** item controls the display of more than one
result on the display. This setting is not preserved.

### Note filter

The **fundamental filter** item enables the fundamental filter.

The **note filters** item enables the note and octave filters.

#### Note filter

Pick which notes you would like to appear in the display.

#### Octave filter

Pick which octaves you would like to appear in the display.

### Strobe

#### Strobe

The **strobe** item controls the strobe display.

#### Strobe colours

If the strobe is enabled, there is an option to change the
**colours**. There are three predefined settings and a custom
setting. If the custom setting is selected the two colours may be
changed individually. touch the outer coloured circle to select a
colour. Touch the centre coloured circle for each colour to see what
it will look like on the left.

![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Tuner-reference.png) &nbsp; ![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Tuner-transpose.png)

### Reference

The **reference** frequency for 'A' may be changed over a limited
range. This will be remembered, so change it back to 440Hz before you
quit the app.

The **transpose** item allows transposition of the note display. This
will be remembered, so change it back if no longer required.

### Temperament

The **temperament** item enables the use of different
temperaments. This will be remembered, so change it back to **equal**
if no longer required.

### Key

The **key** item enables the use of different keys with the selected
temperament. It makes no difference in the **equal** temperament.

### Theme

The **dark theme** item allows use of the dark theme.

### About

The **about** item shows the copyright and licence.

## Building

You can either import the app into Android Studio or use standalone Gradle.
Don't use installed gradle unless it's the same version as here (see
`./gradle/wrapper/gradle-wrapper.properties`).

```shell
$ ./gradlew [assembleDebug|assemble|build]
```
