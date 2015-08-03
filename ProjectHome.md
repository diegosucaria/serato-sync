

# Versions / History #
  * **0.1.4 (the latest)**, released on Sep 26, 2010. Download: http://serato-itch-sync.googlecode.com/files/itch-sync-0_1_4.jar
    * fixed a bug where tracks were showing up as missing when using a library located on external hard drive
    * improved performance of the tool when library sync is performed using an external hard drive
    * added experimental support for video files (.mov, .mp4, .m4a, .avi, .flv, .mpg, .mpeg, .dv, .qtz)
    * 'Subcrates' folder is now created if it doesn't exist, so the tool works correctly with fresh Serato ITCH/ScratchLIVE install

  * 0.1.3, released on Sep 20, 2010. Download: http://serato-itch-sync.googlecode.com/files/itch-sync-0_1_3.jar
    * addressed compatibility issue with Java 1.5, now both 1.5 and 1.6 are fully supported
    * slightly improved error reporting

  * 0.1.2, released on Sep 18, 2010. Download: http://serato-itch-sync.googlecode.com/files/itch-sync-0_1_2.jar
    * added a feature allowing to clear Serato database before sync
    * fixed a bug when top-level music folders without any sub-folders were not showing up as parent crates

  * 0.1.1, released on Sep 4, 2010. Download: http://serato-itch-sync.googlecode.com/files/itch-sync-0_1_1.jar
    * added support for .ogg, .aac, .alac, .aif, .wav
    * fixed a bug when tracks were showing up as missing after import on Windows version of Serato
    * fixed a bug when itch-sync was forcing "include subcrate tracks" behavior on Windows version of Serato

  * 0.1, released on Aug 26, 2010. Download: http://serato-itch-sync.googlecode.com/files/itch-sync-0_1.jar
    * initial support for library conversion, only .mp3 files are scanned

# Introduction #
serato-itch-sync is a helpful utility which allows to map your music collection to Serato ITCH library.

It is very easy to use - you just need to specify the location of your music collection and the location of Serato ITCH library. After that the tool will map your directory structure to crates and put all tracks inside -- so at the end you will end up with having one-to-one hierarchical mapping, one crate (or subcrate) for each directory with your music.

# Why the tool was created and why should I use it? #
Well, I personally don't let iTunes to organize and manage my music library. Just because I want a different layout of my music on the file system. Unfortunately Serato ITCH doesn't really support "sync" with the file system, so you have to create the initial crate structure manually. And even worse - if you put new media files into the existing directories, Serato ITCH will not sync with crates and there is no even an option to force the sync.

So, the tool is going to be really useful for you if:
  * you are a DJ using Serato ITCH
  * you would prefer to manage you music files on your own and need to keep Serato ITCH with your music library

# How does it work? #
As it was mentioned earlier, you have to specify two parameters - the location of your music collection, and the location of Serato ITCH library. The tool will:
  * scan your entire music collection in the location you specified, including all files in all sub-directories
  * populate Serato ITCH library with the corresponding crates, subcrates, and tracks from your music library. If some of the crates already exist, they will be overwritten (which is a good thing, as you can rely the tool and avoid any manual sync actions). Beat grids and all other file-specific parameters will be preserved, as Serato ITCH keeps them in a different place

# How do I run it ? #

The installation process is very simple and it consists of two steps.

First of all, you download the latest version and put it into any directory/folder on your computer. Let's say "`~/serato-itch-sync`" on Mac OS, or "`C:\serato-itch-sync`" on Windows.

Second, you need to create the file with settings called "`itch-sync.properties`" in the same directory with the following contents (of course, replacing the paths to your libraries).

## Mac OS ##
For Mac OS, the properties file should look like:
```
# mode - gui vs. cmd
mode=gui

# path to your personal music collection
music.library.filesystem=/Users/ralekseenkov/Music/iTunes/iTunes Music/Music

# path to your serato library
music.library.itch=/Users/ralekseenkov/Music/_Serato_
```

**Important notes for Mac OS users:**
  * make sure to use forward slash in the library paths
  * the easiest way to create a properties file is to open a "TextEdit", go to "Format" menu and select "Make Plain Text", then enter the contents, and finally "File" and "Save As" giving it "`itch-sync.properties`" name.

## Windows ##
For Windows, the properties file should look like (alternatively, you can replace double backslash `\\` with a single forward slash `/`):
```
# mode - gui vs. cmd                                                          
mode=gui                                                                      

# path to your personal music collection
music.library.filesystem=C:\\Music

# path to your serato library                                            
music.library.itch=C:\\Documents and Settings\\ralekseenkov\\My Documents\\My Music\\_Serato_
```

**Important notes for Windows users:**
  * make sure to use double backslash in the library paths
  * the easiest way to create a properties file is to open "Notepad", enter the contents, and then "File" and "Save As" giving it "`itch-sync.properties`" name. Make sure you saving the file as type "All﻿ Files", so that "Notepad" doesn't add ".txt" extension to the file name

## Graphical vs. Command-Line execution mode ##
As you can see, there is an extra parameter in the config file, which controls the execution mode. If you are not an experienced user, it is recommended to keep the value set to "gui" (GUI means graphical user interface). If you have no issues with the command line, you can change the setting to "cmd" avoiding popup windows on your screen and achieving full automation. If GUI can not be initialized for some reason, the tool will fallback to the command-line mode automatically.

So, it's all set, and now you just need to execute the tool. In both Mac OS and Windows you can just double click the icon and it will launch the jar file (Mac OS will run jar laucher, while Windows will run java.exe or javaw.exe). Or, alternatively, you can just type "`java -jar itch-sync-<version>.jar`" in the command line inside the directory where the tool is installed.

# Optional settings #

## Clear Serato library prior to sync ##
The option is useful if you want to completely Serato database before sync, erasing all previously existing crates and tracks from it, and then make a full sync with a folder on your filesystem.

By default it is disabled, so the sync preserves all existing crates and subcrates in Serato.

When enabled, it deletes all files from directories "Crates" and "Subcrates" (that clears up all existing crates in Serato), and it also deletes the file "database V2" (that clears up all existing tracks from "All" view in Serato) before the sync.

To enable, add the following lines to your properties file:
```
# deletes all existing crates from serato library prior to sync, as well as all tracks from "All" view
music.library.itch.clear-before-sync=true
```

# Screenshots #
## Before sync ##
![http://serato-itch-sync.googlecode.com/svn/trunk/images/01-before.png](http://serato-itch-sync.googlecode.com/svn/trunk/images/01-before.png)

## Running the tool ##
![http://serato-itch-sync.googlecode.com/svn/trunk/images/02-run.png](http://serato-itch-sync.googlecode.com/svn/trunk/images/02-run.png)

## Original music collection ##
![http://serato-itch-sync.googlecode.com/svn/trunk/images/03-files.png](http://serato-itch-sync.googlecode.com/svn/trunk/images/03-files.png)

## Imported into Serato ITCH ##
![http://serato-itch-sync.googlecode.com/svn/trunk/images/04-after.png](http://serato-itch-sync.googlecode.com/svn/trunk/images/04-after.png)

**Warning:** it is recommended to backup your Serato ITCH library. It is usually located in "`~/Music/_Serato_`". Specifically, the tool is modifying directories "Crates" and "Subcrates" inside.

**Warning:** please keep Serato ITCH closed before running the tool

# FAQ #

## I don't have Java installed on my computer. Will the tool work? ##
No. You need to have Java >= 1.5 installed on your computer to run the tool.

## What versions of Serato ITCH are supported? ##
Tested with 1.7 final release (17030), as well as with 1.7 RC2, RC3.

## Does it work with any versions of Serato Scratch Live? ##
Tested with 2.1.0 (21057) and it seems to work fine!

## Will the tool delete any data from my Serato? ##
No. The tool works only with crates/subcrates and tracks within them. So, every single Serato setting (e.g. global settings, play history, track color coding, id3 tags, beat grids, etc) is preserved.

**Warning:** if you enable "Clear Serato library prior to sync" option, then the tool will delete all your existing crates and tracks from Serato before running sync. Enable it only if you know what you are doing.

# Community & Support #
Don't hesitate to report bugs and enhancements using the "Issues" tab. I will try to address them if/when I have time.

Also, I would be happy to see more contributors to the tool! Please contact me if you are interested.