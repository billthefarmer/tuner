Android Tuner

To import this project into the Android ADT you will need to add a
.project file here. The easiest way to do that is to copy one from
another project and edit the

<projectDescription>
<name>Tuner</name>

line to change the name. Or create a new project and copy all the
files.

If you want to use the Android SDK, you will need to enter

android update project -p <project folder>

from the command line or terminal substituting the project's folder
for <project folder>. Then build it with ant. I don't understand ant,
so I'm not going there.

