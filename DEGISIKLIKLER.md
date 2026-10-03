# Sorumluluk Takibi — İnceleme ve Düzenleme Notları

Bu dosya, projede tespit edilen sorunları ve yapılan değişiklikleri özetler.

## 1. Kritik hatalar (uygulamayı fiilen bozuyordu)

| # | Sorun | Etki | Durum |
|---|-------|------|-------|
| 1 | `initializeDatabaseIfEmpty` her açılışta demo öğrencileri ve kayıtlı kullanıcıları yeniden yazıyordu | Öğretmenin sildiği kayıtlar geri geliyor, gerçek öğrencilerin "bugün tamamlanan" sayısı sıfırlanıyordu | Düzeltildi — demo veri yalnızca ilk kurulumda bir kez yazılıyor |
| 2 | Günlük program (`daily_schedules`) yalnızca ilk gün için oluşturuluyordu | Ertesi gün uygulama açıldığında görev listesi **tamamen boş** kalıyordu | Düzeltildi — `ensureScheduleForDate` her gün otomatik liste üretiyor |
| 3 | `todayDateString` ViewModel oluşturulurken bir kez hesaplanıyordu | Gece açık kalan uygulamada tarih değişmiyordu | Düzeltildi — dakikalık kontrol + uygulama öne geldiğinde yenileme |
| 4 | Yeni kayıt olan öğrenci için `student_profiles` kaydı **hiç açılmıyordu** | Yeni öğrenci, demo öğrenci "Ahmet Yılmaz"ın verilerini görüyordu | Düzeltildi — kayıtta profil + program oluşturuluyor, cihazda kalıcı saklanıyor |
| 5 | `approveTask` içinde `updateClassmateCompleted(studentId, 13)` — sabit 13 yazılıyordu | Öğretmen panosundaki tamamlama sayıları uydurmaydı | Düzeltildi — sınıf listesi artık gerçek kayıtlardan canlı türetiliyor |
| 6 | QR kod üreteci gerçek QR standardını uygulamıyordu (hata düzeltme kodu yok, maskeleme yok) | Üretilen karekod **hiçbir kamerayla okunamıyordu** | Düzeltildi — ZXing ile standart QR üretimi |
| 7 | `fallbackToDestructiveMigration()` | Her şema değişikliğinde kullanıcının tüm verisi siliniyordu | Düzeltildi — gerçek migration (v7→v8) yazıldı |
| 8 | Uygulamada hiç kimlik doğrulama yokken Firestore kuralları `request.auth != null` istiyordu | Bulut senkronizasyonunun tamamı sessizce reddediliyordu | Düzeltildi — anonim oturum açılıyor |
| 9 | `kotlinx-coroutines-play-services` bağımlılığı yoktu ama `.await()` kullanılıyordu | Derleme, geçişli bağımlılığa bel bağlıyordu | Düzeltildi — açıkça eklendi |
| 10 | `deleteStudent` fonksiyonu yalnızca istatistik satırını siliyor, profil/kayıt/görevleri bırakıyordu | Silinen öğrenci kısmen sistemde kalıyordu | Düzeltildi — tek transaction'da tam temizlik |

## 2. Mantık ve veri tutarlılığı

- **Elmas ödülü**: Onayda her zaman sabit `+5` veriliyordu; görevin kendi `diamondReward` değeri yok sayılıyordu. Artık görevin gerçek değeri kullanılıyor.
- **Çift onay**: Zaten onaylanmış görev tekrar onaylanınca elmas ikinci kez ekleniyordu. Engellendi.
- **Reddetme**: Onaylanmış bir görev reddedilince verilen elmas geri alınmıyordu; bakiye şişiyordu. Düzeltildi.
- **Seri (streak)**: `streakDays` hiçbir yerde hesaplanmıyordu, tohum verideki 6/14 değerinde donuyordu — dolayısıyla "İstikrar Yıldızı" rozeti sahteydi. Artık gerçek onay geçmişinden hesaplanıyor (bir gün, o günün görevlerinin %60'ı onaylandıysa seriye sayılır; devam eden gün seriyi bozmaz).
- **Toplu onay**: 20 görev için 20 ayrı veritabanı turu atılıyordu. Tek toplu işleme indirildi.
- **Veli doğrulaması**: Doğrulama başarısız olsa bile oturum zaten açılmış oluyordu (tercihler coroutine'den önce yazılıyordu). Artık oturum yalnızca doğrulama başarılıysa açılıyor ve hata mesajı kayıt ekranında görünüyor.
- **Bulut çakışması**: Firestore'dan gelen eski kayıt, yereldeki daha yeni onayı ezebiliyordu. Zaman damgası kontrolü eklendi.

## 3. Performans

- `tasks`, `task_logs`, `daily_schedules`, `registered_users` tablolarına indeks eklendi (sorgular tam tablo taraması yapıyordu).
- `classmate_stats` tablosu ikinci bir doğruluk kaynağıydı ve bayatlıyordu; artık profillerden canlı türetiliyor.
- Akışlara `distinctUntilChanged` eklendi — gereksiz recomposition azaldı.
- Kullanılmayan bağımlılıklar kaldırıldı: Retrofit, Moshi (+KSP işlemcisi), OkHttp, logging-interceptor, firebase-ai, App Check. Hiçbiri kodda kullanılmıyordu; derleme süresini ve APK boyutunu büyütüyorlardı.
- Release derlemesinde `isMinifyEnabled` ve `isShrinkResources` açıldı, R8 kuralları yazıldı.
- QR çizimi: yan yana modüller tek dikdörtgen olarak çiziliyor (841 çizim çağrısı yerine çok daha azı).

## 4. Güvenlik

- **Firestore kuralları**: Önceki sürümde neredeyse tüm koleksiyonlarda `allow read: if true` vardı — sınıftaki her öğrencinin adı, elması ve günlük performansı internetteki herkese açıktı. Artık okuma da oturum gerektiriyor, tanımsız yollar tamamen kapalı, `status` ve `diamonds` alanlarına değer doğrulaması eklendi.
- Öğretmen PIN'i hâlâ istemci tarafında ve varsayılan `1234`. En azından 4-8 haneli olma doğrulaması eklendi; kalıcı çözüm için PIN'in sunucuya taşınması gerekir (aşağıya bakınız).
- `try { } catch (_: Exception) {}` şeklindeki sessiz yutma blokları `runCatching` + `Log.w` ile değiştirildi; artık sorunlar logcat'te görünüyor.

## 5. Hâlâ yapılması gerekenler (bu düzenlemenin kapsamı dışında)

1. **Gerçek kimlik doğrulama**: Şu an anonim oturum var. Rol bazlı yetki (`role` custom claim) için Cloud Functions / Admin SDK tarafı gerekiyor. Aksi hâlde "öğretmen" yetkisi yalnızca istemcide.
2. **Öğretmen PIN'i**: İstemcideki yerel veritabanında düz metin. Sunucuya taşınmalı.
3. **google-services.json yok**: Dosya eklenmeden bulut senkronizasyonu çalışmaz (uygulama bu durumda tamamen yerel çalışır, çökmez).
4. **Bildirimler**: "Veli onayı bekliyor" gibi bir push bildirimi yok; ürünün kullanım sıklığını en çok artıracak eksik muhtemelen budur.
5. **Geçmiş/rapor ekranı**: Veriler günlük tutuluyor ama haftalık/aylık geçmiş görüntüleme ekranı yok.
6. **Test**: Mevcut testler şablondan gelme (boş bir `Text` ekran görüntüsü). Onay akışı, seri hesabı ve 20 görev kuralı için birim testi yazılmalı.
7. **AGP 9.1.1 / Compose BOM 2024.09** sürüm farkı büyük; Compose BOM'u güncel bir sürüme almak faydalı olur.

## 6. Derleme notu

Bu düzenleme Android SDK'sı olmayan bir ortamda yapıldığı için **derlenerek doğrulanmadı**. Android Studio'da ilk açılışta `Gradle Sync` + `Build > Rebuild Project` çalıştırıp olası küçük uyarıları gidermeniz gerekebilir.
