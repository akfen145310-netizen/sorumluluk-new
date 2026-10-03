# ---- Sorumluluk Takibi / R8 kuralları ----

# Firestore, doc.toObject(...) çağrısında yansıma (reflection) kullanır.
# Bu veri sınıflarının alan adları korunmazsa bulut senkronizasyonu sessizce bozulur.
-keepclassmembers class com.example.data.firebase.DailyLog { *; }
-keep class com.example.data.firebase.DailyLog { <init>(...); }

# Room tarafından üretilen sınıflar
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-dontwarn androidx.room.paging.**

# Firebase / Play Services
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ZXing (yalnızca kodlayıcı kullanılıyor)
-dontwarn com.google.zxing.**

# Kotlin coroutines
-dontwarn kotlinx.coroutines.**

# Kilitlenme raporlarının okunabilir kalması için satır numaralarını koru
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
