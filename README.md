Important priority and Jenkins configurations

3, Exo player old
   https://github.com/quicklearner4991/Exoplayer-VideoPlayer-in-Recyclerview

   Exo player new (current) added 27/04/2023
   https://github.com/tiendatit92/ExoplayerFullscreenLandcapse

4, PLay feed red color
   app:played_color="#ff0000"

5, Custom gallery
   https://github.com/datanapps/MultipleMediaPicker

6, Retrofit multipart (camera and galery) (current) added 26/04/2023
   https://github.com/khaliqdadmohmand/upload_file_php_android

 ### Previous version
    private static final String APP_VERSION = "1907104";
    private static final String APP_VERSION = "2004091";
    private static final String APP_VERSION = "2004201";
    private static final String APP_VERSION = "2004211";
    private static final String APP_VERSION = "2004262";
    private static final String APP_VERSION = "2004281";
    private static final String APP_VERSION = "2005011";
    private static final String APP_VERSION = "2005012";
    private static final String APP_VERSION = "2005021";
    private static final String APP_VERSION = "2005041";
    private static final String APP_VERSION = "200621";
    private static final String APP_VERSION = "200904";
    private static final String APP_VERSION = "2009041";
    private static final String APP_VERSION = "2103161";
    private static final String APP_VERSION = "2201308";
    private static final String APP_VERSION = "2201311";

 ### Jenkins 

 ## Jenkins errors solutions

1, jenkins android Execution failed for task ':app:connectedDebugAndroidTest'.
   solution :  Add this dependency to get this class:
   debugImplementation "androidx.test:monitor:1.6.1"
   url : https://stackoverflow.com/questions/74608921/android-studio-instrumented-test-stuck-at-task-appconnecteddebugandroidtest

   2, INSTALL_FAILED_UPDATE_INCOMPATIBLE
   solution :  remove the old one and try again.
   url : https://stackoverflow.com/questions/11891848/install-failed-update-incompatible-when-i-try-to-install-compiled-apk-on-device

   3, Tests on Infinix X689 - 11 failed: Instrumentation run failed due to 'Process crashed.'
   solution :  android update to AndroidX, therefor I needed also to update my build.gradle

   testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
   to
   testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
   url : https://stackoverflow.com/questions/52605267/instrumentation-run-failed-due-to-process-crashed

   4, doesn’t match anything: even ‘C:’ doesn’t exist (Files to archive)
   solution : **/*.apk
   url : https://k21academy.com/devops-foundation/ci-cd-pipeline-using-jenkins/