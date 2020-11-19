# FirewallHack
This is an opensource java application that will permit to all the the ELIS students and guest to automate the login procedure onto the firewall.
It is based on Java 8 SDK and use a self find research of the necessary driver to drive the different type of Web Browser

# Environment Settings
To let the application work is necessary to install the Java JDK version 8 from the apache website.
Once the installation is complete is necessary to add the Path Variable of the jdk /bin folder onto the own operative system.
To do that you can simply research it on the official Java website https://www.java.com/it/download/help/path.html

# Compiling Settings(for those who want to do)
Since the whole project is already compiled and exported in a JAR File, it is not necessary do compile it with Maven Dependencies.
Who wants to do that have to download the binary of Maven from the Official Website https://maven.apache.org/download.cgi and than, 
like for the Java JDK Environment Variables setting up process, you have to set the /bin folder of Maven into Path Variables.
Remember, if you do that you should move the entire BinaryMavenFolder from the download location to a safe folder like the root of the OS disk.
After that, compile the code using<br />
  <p align="center">mvn clean install</p><br />
 it will return a JAR file into the /target folder
 
# How to Run the JAR File
To run the JAR you can have two choice. The first is to use directly the command line typing the command<br />
  <p align="center">java -jar FirewallHack.jar</p><br />
or you can create a .bat fle that contains the previous command and run it directly.
To let it work correctly, you should specify the path of the JAR File(if it is not in the same folder of the .bat file)

# Parameters
When you run the JAR using the command we said before, you have to pass three different pasaneters that are --> Browser, Username, Password
(in the order as we write here)
the most common supported browser are chrome, opera, mozilla, edge, ie.
Example of launch<br />
  <p align="center">java -jar FirewallHack.jar chrome userpippo passwordpippo</p><br />

# Enjoy surfing on the PornFreeInternet
