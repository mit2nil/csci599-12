# How-to

*Useful references*
  * [How to use external jar](http://www.programcreek.com/2014/01/compile-and-run-java-in-command-line-with-external-jars/)
  * [Download Google Gson jar] (http://search.maven.org/#artifactdetails|com.google.code.gson|gson|2.6.1|jar)
  * [Gson reference] (https://github.com/google/gson)

*Compile and Run (Windows)*
  * javac -cp ".;tika-app-1.12.jar;gson.jar" tagratio.java
  * java -cp ".;tika-app-1.12.jar;gson.jar" tagratio

*Compile and Run (Linux/Mac)*
  * javac -cp ".:tika-app-1.12.jar:gson.jar" tagratio.java
  * java -cp ".:tika-app-1.12.jar:gson.jar" tagratio