# Aion Lightning Emulator 2.7

This project is for people that are looking for an old school emulator for Aion. It is based on first version of Aion Lightning 2.7 (from october 2011) and has been used in 2 different private servers.
If you played on these old school servers and think you can maker better place to play Aion 2.7, please feel free to download this emulator and install it on your own server, and try to satisfy this trully pest community :-)

## Getting Started

This is what you need to make it work great. I'll give you my own environment, where I know that the server is correctly working. Be awaire that I won't provide any support for any problem outside the emulator itself (for example MySQL errors, Java installation, ant support, etc.)

### Prerequisites

As it is an old Aion Emulator, you need an old version of Java to make it work correcty. This emulator is using some integrated functions that were deprecated in Java 1.6 and are not used anymore in Java 1.7 and later. So to make it work you'll need :
- Java 1.6 (download and install __ONE__ of these programms regarding your OS)
	* [Official release](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html)
if you have an Oracle account (you can create one, it's free)

	* [OpenJDK 6](http://openjdk.java.net/projects/jdk6/)
- A Java builder
	* [Ant](http://ant.apache.org/) (recommended as I know it's working and some customs have been added to build.xml file)
	* [Maven](https://maven.apache.org/)
- MySQL (Last version should work, not tested with MariaDB or other DBMS)
- Maybe more prerequisites will come later


### Installing

- There is nothing particular to do for installing this emulator, just check all prerequisites and use last SQL file for each server to create database structure.
- You should run a build directly on the server you will use to create game server ; then if it works, you can move zip files anywhere, then unzip them and try to launch bat or sh files

## Contributing

Please always test your code before pull any change request. You should correctly explain what your change request will fix / add in this emulator.

## Authors and contributors before GitHub

* **Aion Lightning** - *Initial work*
* **Ferosia** - *First commits and patches* Still on this project :)
* **Metos** - *Hard work to make it working*
* **Crysis** - *Hard work to make Metos working :-)*
* **Seita** - *Hard work to make it beautiful*
* **Krunchy** - *Hard work to make it beautiful*
* **Keiryu** - *Hard work to make it unique*

## License

This project is not licensed, please take in consideration license instructions on each files of this project.
Be respectfull of work done by other, do not hesitate to inform if you find any issue.
