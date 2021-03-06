# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
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
#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

#优化  不优化输入的类文件
-dontoptimize

#预校验
-dontpreverify

#混淆时是否记录日志
-verbose

# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解
-keepattributes Signature,Annotation

############# region for xUtils
-keep public class org.xutils.** {
    public protected;
}
-keep public interface org.xutils.* {
    public protected;
}
-keepclassmembers class * extends org.xutils.* {
    public protected;
}
-keepclassmembers @org.xutils.db.annotation. class * {
    ;
}
-keepclassmembers @org.xutils.http.annotation. class * {
    *;
}
-keepclassmembers class * {
    @org.xutils.view.annotation.Event;
}
############## end region
