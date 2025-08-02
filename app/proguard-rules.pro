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

# 保留Room数据库相关类
-keep class com.example.expresscodeassistant.database.** { *; }
-keep class com.example.expresscodeassistant.dao.** { *; }
-keep class com.example.expresscodeassistant.model.** { *; }

# 保留Kotlin相关类
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# 保留AndroidX相关类
-keep class androidx.** { *; }
-dontwarn androidx.**

# 保留自定义视图和组件
-keep class com.example.expresscodeassistant.adapter.** { *; }
-keep class com.example.expresscodeassistant.receiver.** { *; }
-keep class com.example.expresscodeassistant.widget.** { *; }