# jEPlus
jEPlus source code, maven and NetBeans projects. More information about jEPlus including users manual can be found on http://www.jeplus.org/. Binary distributions are available at https://sourceforge.net/projects/jeplus/

## How to build the project

If you have **NetBeans IDE**, download the source package and open it as a NetBeans project.

Otherwise you can use **Apache Maven** to build the project directly:

  - Install JDK (at least v1.7): http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html
  - Install Apache Maven: https://maven.apache.org/install.html
  - Download the source pack and extract the contents to a folder
  - Open a terminal window and change into the jEPlus source folder
  - run "mvn package"
  - If build is successful, the "jeplus-?.?.?-jar-with-dependencies.jar" in the target/ folder is the single file for distribution
