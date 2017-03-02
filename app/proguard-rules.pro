# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/gavin/Develop/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keep public class cn.gavinliu.bus.station.R$*{
public static final int *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# OkHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }

 # OrmLite
 -keepattributes *DatabaseField*
 -keepattributes *DatabaseTable*
 -keepattributes *SerializedName*
 -keep class com.j256.**
 -keepclassmembers class com.j256.** { *; }
 -keep enum com.j256.**
 -keepclassmembers enum com.j256.** { *; }
 -keep interface com.j256.**
 -keepclassmembers interface com.j256.** { *; }

 # Gson
# -keepattributes Signature-keepattributes *Annotation*
 -keep class sun.misc.Unsafe { *; }
 -keep class com.google.gson.stream.** { *; }
 -keep class cn.gavinliu.bus.station.entity.** { *; }

 # Retrofit
 -dontwarn retrofit2.**
 -keep class retrofit2.** { *; }
 -keepattributes Signature
 -keepattributes Exceptions

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
