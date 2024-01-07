

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
-assumenosideeffects class android.util.Log {
public static *** d(...);
public static *** v(...);
public static *** i(...);
public static *** w(...);
public static *** e(...);
}
-dontwarn okhttp3.internal.platform.*
-dontwarn android.support.v4.**
-dontwarn com.facebook.ads.**
-keep class android.support.v4.* { *; }
-repackageclasses "com.example.chromecastone"

-keep class chromecastone.ClassThatUsesObjectAnimator { *; }

-keepclassmembers class chromecastone{
   public *;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep public class com.google.android.gms.ads.** {
   public *;
}

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-dontwarn com.squareup.okhttp.**
-dontwarn org.apache.lang.**

-keep public class com.google.ads.** {
   public *;
}

-keep public class android.support.v7.widget.* { *; }
-keep public class android.support.v7.internal.widget.* { *; }
-keep public class android.support.v7.internal.view.menu.* { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
-keep class android.support.v7.widget.RoundRectDrawable { *; }


-keep class com.facebook.* { *; }
-keepattributes Signature

 -keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,LineNumberTable, Annotation, EnclosingMethod
 -dontwarn android.webkit.JavascriptInterface

 -keep public class * extends android.view.View{
     *** get*();
     void set*(***);
     public <init>(android.content.Context);
     public <init>(android.content.Context, java.lang.Boolean);
     public <init>(android.content.Context, android.util.AttributeSet);
     public <init>(android.content.Context, android.util.AttributeSet, int);
 }