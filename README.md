# cryptox
cryptox - 256bit aes/gcm file encryptor with pbkdf2

# information about
- Java
- SecurityProvider: BouncyCastle
- GUI with SceneBuilder
- jar2app used to create a macos Application (https://github.com/Jorl17/jar2app)

# howto use BouncyCastle
- default JAVA HOME folder (mac os): /Library/Java/JavaVirtualMachines/jdk***.jdk/Contents/Home/
- download bcpkix, bcprov-ext and bcprov from https://www.bouncycastle.org/latest_releases.html and copy it into /Library/Java/JavaVirtualMachines/jdk***.jdk/Contents/Home/jre/lib/ext Folder
- download the unlimited restriction policy from https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html to use strong cryptography in Java and copy it into /Library/Java/JavaVirtualMachines/jdk***.jdk/Contents/Home/jre/lib/security Folder


# howto export a JavaProject with BouncyCastle to a jar
- add bcpkix-*  bcprov-ext-* and bcprov-* and the US_export_policy to the Java BuildPath
- write explicit Security.addProvider(new BouncyCastleProvider()); in your code
- export as Runnable JAR File -> Package required libraries into generated JAR

# howto create a macos apllication out of a jar file (with icon)
- clone https://github.com/Jorl17/jar2app
- cd into the cloned folder
- ./jar2app /PATH/TO/YOUR/JAR
- a macos application will be created in the cloned folder
- right click on the apllication -> Show Package Contents 
- copy the icons (for example .icns-file) into Contents/Resources
- edit Contents/info.plist -> set value in the row Icon File to ICONNAME.icns
