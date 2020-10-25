##打印混淆信息
#-verbose
##代码优化选项，不加该行会将没有用到的类删除，这里为了验证时间结果而使用，在实际生产环境中可根据实际需要选择是否使用
-dontshrink
#-dontwarn android.support.annotation.Keep
##保留注解，如果不添加改行会导致我们的@Keep注解失效
#-keepattributes *Annotation*
#-keep @androidx.annotation.Keep class *

-keep class * extends androidx.room.RoomDatabase
-keep class androidx.annotation.Keep

-keep @androidx.annotation.Keep class *

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}