# Setting up OpenGL in Code::Blocks 16.01

The following setup is for Windows 10/8.1/8/7 (Home/Single Language/Enterprise/Ultimate ..) 64 bit machine (win_x64) as of __Nov 28, 2017__.
But the setup is same (with minute difference in directories) for Windows 10/8.1/8/7/XP 32 bit machine(win_x86).

List of things which you need to operate the code
- [Code::Blocks 16.01](https://sourceforge.net/projects/codeblocks/files/Binaries/16.01/Windows/codeblocks-16.01-setup.exe/download).
- [MinGW](http://www.mingw.org/).
- [FreeGlut](https://www.transmissionzero.co.uk/files/software/development/GLUT/freeglut-MinGW.zip).
- [SOIL](www.lonesock.net/files/soil.zip).

Install MinGW before doing anything. The system files should be there in `C:\MinGW` after installing.

## FreeGlut setup
- Unzip the file. Open it, go to `bin` , choose **freeglut.dll** (I know there's a x64 folder which contains another freeglut.dll too, DONT TOUCH THAT). Copy the dll and paste it to the following locations.
``
C:\Windows
C:\Windows\System
C:\Windows\System32
C:\Windows\SysWOW64
``
set your PATH variable to `C:\Windows\SysWOW64`.
- Now go back, and then go to `lib`, select **libfreeglut.a** and **libfreeglut_static.a** (Again, don't go into x64 folder), paste it to `C:\MinGW\lib`.
- Now go to `include\GL`, copy all the header files and paste it to `C:\MinGW\include\GL`.

## SOIL setup
- Unzip the file. Open it, go to `src`, copy everything (all headers and `original` folder too) and paste it to `C:\MinGW\include`.
- Now go back, go to `lib`, copy **libSOIL.a** and paste it to `C:\MinGW\lib`.

## Code::Blocks setup
- Install as you usually do. 
- Now open Notepad (run it as administrator). Click on File -> open. Open `C:\Program Files (x86)\CodeBlocks\share\CodeBlocks\templates` open __glut.cbp__. Click on Edit -> Replace All . Type "Glut32" replace it with "freeglut". There should be only one instance to be replaced. Save it. Click File -> Exit.
- Now open Notepad (run it as administrator). Click on File -> open. Open `C:\Program Files (x86)\CodeBlocks\share\CodeBlocks\templates\wizard\glut` open __wizard.script__. Click on Edit -> Replace All . Type "Glut32" replace it with "freeglut". There should be two instances to be replaced. Save it. Click File -> Exit.
- Open Code::Blocks. Go to Settings -> Compiler Settings -> Linker settings. Add `libfreeglut.a, libfreeglut_static.a, libglu32.a, libopengl32.a, libSOIL.a` from `C:\MinGW\lib`. In other linker settings, add (as it is) `-lglu32 -lfreeglut -lfreeglut_static -lSOIL - lopengl32`.
