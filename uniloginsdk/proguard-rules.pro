# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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
-dontwarn com.geetest.onelogin.**
-keep class com.geetest.onelogin.** {
*;
}
-dontwarn com.geetest.onepassv2.**
-keep class com.geetest.onepassv2.** {
*;
}
-dontwarn com.geetest.deepknow.**
-keep class com.geetest.deepknow.** {
*;
}
-dontwarn com.geetest.mobinfo.**
-keep class com.geetest.mobinfo.** {
*;
}
-dontwarn com.cmic.sso.sdk.**
-keep class com.cmic.sso.sdk.** {
*;
}
-dontwarn com.unigeetest.**
-keep class com.unigeetest.** {
*;
}
-dontwarn cn.com.chinatelecom.account.**
-keep class cn.com.chinatelecom.account.** {
*;
}
#emay
-keep class cn.emay.ql.uniloginsdk.listeners.**{*;}
-keep class cn.emay.ql.uniloginsdk.utils.**{*;}
-keep class cn.emay.ql.uniloginsdk.UniSDK{*;}