sphero-bluemix-android
================================================================================

This [project](https://github.com/IBM-Bluemix/sphero-bluemix-android) is a simple Android app that demonstrates how to steer a [Sphero](http://www.gosphero.com/sphero/) ball via URL commands using [IBM Bluemix](http://bluemix.net), the [MQTT](http://mqtt.org) protocol and the [IBM Internet of Things](https://console.ng.bluemix.net/?ace_base=true#/store/serviceOfferingGuid=8e3a9040-7ce8-4022-a36b-47f836d2b83e&fromCatalog=true) service. Watch the [video](http://heidloff.net/nh/home.nsf/article.xsp?id=02.03.2015083022NHEATJ.htm) and read the [slides](http://heidloff.net/nh/home.nsf/article.xsp?id=16.03.2015093840NHEC68.htm) to learn more.

<iframe src="//www.slideshare.net/slideshow/embed_code/key/GojsUXfMPU2Aty" width="425" height="355" frameborder="0" marginwidth="0" marginheight="0" scrolling="no" style="border:1px solid #CCC; border-width:1px; margin-bottom:5px; max-width: 100%;" allowfullscreen> </iframe>

For the communication between the Android app and IBM Bluemix the [MQTT client Paho](https://www.eclipse.org/paho/clients/java/) from Eclipse is used that comes with the project. For the communication between the Android app and the ball over bluetooth the [Sphero Android SDK](https://github.com/orbotix/Sphero-Android-SDK) is used. Since the SDK is not available under an open source license three files need to be downloaded and copied into the Android project.

Author: Niklas Heidloff [@nheidloff](http://twitter.com/nheidloff)


Import of the Android Project
----------------------------------------------------------------------------------

Download these files from the Sphero SDK.

* [RobotLibrary.jar](https://github.com/orbotix/Sphero-Android-SDK/blob/master/library/libs/RobotLibrary.jar)
* [armeabi/libachievement_manager.so](https://github.com/orbotix/Sphero-Android-SDK/blob/master/library/libs/armeabi/libachievement_manager.so)
* [armeabi-v7a/libachievement_manager.so](https://github.com/orbotix/Sphero-Android-SDK/blob/master/library/libs/armeabi-v7a/libachievement_manager.so)

In the easiest case copy the files to the directories as documented in the screenshots before importing the project in Android Studio.

* [RobotLibrary.jar](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/dependency1.png)
* [armeabi/libachievement_manager.so](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/dependency3.png)
* [armeabi-v7a/libachievement_manager.so](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/dependency2.png)

After this open Android Studio and import the project. This should result in the following project structure - [Project](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/projectstructure1.png), [Android](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/projectstructure2.png).

Setup of the Bluemix Application and the Node-RED Flow
----------------------------------------------------------------------------------

In order to send commands to the Android app a Node-RED flow in IBM Bluemix is used in combination with the IBM Internet of Things Foundation. 

Log in to Bluemix and create a new application, e.g. MySphero, based on the [Internet of Things Foundation Starter](https://console.ng.bluemix.net/?ace_base=true#/store/appType=web&cloudOEPaneId=store&appTemplateGuid=iot-template&fromCatalog=true). Additionally add the [Internet of Things](https://console.ng.bluemix.net/?ace_base=true#/store/serviceOfferingGuid=8e3a9040-7ce8-4022-a36b-47f836d2b83e&fromCatalog=true) service to it.

In the next step you have to register your own device. Open the dashboard of the Internet of Things service and navigate to 'Add Device'. As device type choose 'and' (for Android) and an unique device id - [screenshot](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/registerdevice1.png). As result you'll get an org id and password - [screenshot](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/registerdevice2.png).

In order to import the flow open your newly [created Bluemix application](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/nodered1.png) and open the Node-RED editor, e.g. http://mysphero.mybluemix.net/red, and choose [import from clipboard]((https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/nodered4.png). You find the flow in the sub-directory 'noderedflow'. In the [outgoing IoT node](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/nodered3.png) select your unique device id and deploy the application.

Run the Android App and invoke URLs
----------------------------------------------------------------------------------

Before running the app you need to enter the Internet of Things configuration from the previous step in the Java class net.bluemix.sphero.MainActivity: org, device id and password.

The app can not be run via the emulator but only actual Android devices. Make sure that both Wifi and Bluetooth are enabled.

Launch the app 'Sphero Bluemix' on your Android device and select your Sphero ball. Once the ball is connect, press the 'Connect' button to also connect to Bluemix. After this you can see the connected device in the IoT dashboard. You can now invoke the following URL commands to steer the Sphero ball.

* [/go](http://mysphero.mybluemix.net/go)
* [/left](http://mysphero.mybluemix.net/left)
* [/right](http://mysphero.mybluemix.net/right)
* [/reverse](http://mysphero.mybluemix.net/reverse)

![alt text](https://raw.githubusercontent.com/IBM-Bluemix/sphero-bluemix-android/master/images/nodered2.png "Flow")