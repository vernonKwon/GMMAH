# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/gwon-ocheol/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#AdMob
-keep public class com.google.android.gms.ads.**{
    public *;
    }

-keep public class com.google.ads.**{
    public *;
}

#Google analytics
 -keep class com.google.android.gms.** { *; }
 -keep public class com.google.android.gms.**
 -dontwarn com.google.android.gms.**

#Jsoup
-keep public class org.jsoup.** {
	public *;
}
-keeppackagenames org.jsoup.nodes

-dontwarn org.apache.commons.codec.**

-keep class yourpakganame.itemdecorator
-keep public class * extends android.support.v7.widget.RecyclerView.ItemDecoration
-keep class android.support.v7.widget.RecyclerView
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keepattributes SourceFile,LineNumberTable #trace

#-keep class PostDTO

-keep class commons-codec.** { *; }
-dontshrink
-verbose #//로그 봄
-dontoptimize #// 압축 하지 않음 그냥 하지말자..