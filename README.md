Android Tuner
=============

![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Tuner-portrait.png)
&nbsp;
![](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Tuner-settings.png)

This project has been tested on the android emulator, a Nexus 10 and a
Nexus 7, nothing else. The emulator worked fine at 8000 samples a
second, the Nexus at 8000, 11025, 16000, 22050 and 44100. The apk may
be found on
[F-Droid](http://f-droid.org/repository/browse/?fdcategory=Multimedia&fdid=org.billthefarmer.tuner&fdpage=1).

To import this project into the Android ADT you will need to add a
.project file here. The easiest way to do that is to copy one from
another project and edit the
~~~xml
<projectDescription>
	<name>Tuner</name>
~~~
line to change the name. Or create a new project and copy all the
files.

If you want to use the Android SDK, you will need to enter
~~~
android update project -p <project folder>
~~~
from the command line or terminal substituting the project's folder
for <project folder>. Then build it with ant. I don't understand ant,
so I'm not going there.

