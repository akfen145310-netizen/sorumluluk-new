# APK Nasıl Alınır / Telefona Nasıl Kurulur

Bu zip kaynak koddur; içinde hazır APK yoktur. Aşağıdaki üç yoldan biriyle APK üretebilirsiniz.

## Yol 1 — Android Studio (en kolay, önerilen)

1. Android Studio'yu açın → **Open** → bu klasörü seçin.
2. İlk açılışta Gradle senkronizasyonunun bitmesini bekleyin (internet gerekir, ilk sefer 5-15 dk).
   Android Studio eksik olan `gradlew` sarmalayıcısını ve `debug.keystore` dosyasını kendisi oluşturur.
3. Üst menü: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
4. Bitince çıkan bildirimde **locate** bağlantısına tıklayın. Dosya şuradadır:
   `app/build/outputs/apk/debug/app-debug.apk`
5. Bu dosyayı telefona kopyalayın (USB, Drive, WhatsApp fark etmez) ve dosya yöneticisinden açıp kurun.

Daha küçük ve hızlı sürüm istiyorsanız Android Studio'da sol alttaki **Build Variants** panelinden
`release` seçip aynı adımları tekrarlayın; çıktı `app/build/outputs/apk/release/app-release.apk` olur.

## Yol 2 — Komut satırı

Bilgisayarda Android SDK ve Gradle 9.3.1 kuruluysa:

```bash
gradle :app:assembleDebug
# çıktı: app/build/outputs/apk/debug/app-debug.apk
```

## Yol 3 — GitHub Actions (bilgisayara hiçbir şey kurmadan)

1. Bu klasörü bir GitHub deposuna yükleyin.
2. Depoda **Actions** sekmesi → **APK Derle** → **Run workflow**.
3. Derleme bitince sayfanın altındaki **Artifacts → sorumluluk-takibi-apk** bağlantısından zip'i indirin;
   içinde APK vardır.

Gerekli iş akışı dosyası `.github/workflows/apk-derle.yml` olarak hazır eklendi.

## Telefonda kurulum

- Dosya yöneticisinden APK'ya dokunun.
- Android "Bilinmeyen kaynaklardan yüklemeye izin ver" diye soracaktır; ilgili uygulamaya (ör. Dosyalar
  veya Chrome) izin verin.
- Google Play Protect bir uyarı gösterebilir — imza Play Store'dan gelmediği için normaldir,
  "Yine de yükle" diyebilirsiniz.

## Notlar

- **Bulut senkronizasyonu için `google-services.json` gerekir.** Firebase konsolundan indirip `app/`
  klasörüne koyun. Koymazsanız uygulama çöker değil, tamamen cihaz içinde (çevrimdışı) çalışır.
- **Google Play'e yükleyecekseniz** kendi yayın anahtarınızı üretmeniz gerekir:
  ```bash
  keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
  ```
  Dosyayı proje köküne koyun ve derlemeden önce `STORE_PASSWORD`, `KEY_PASSWORD` ortam değişkenlerini
  tanımlayın. Anahtar yoksa release APK debug anahtarıyla imzalanır: telefona kurulur ama Play'e yüklenemez.
- `versionCode = 1` durumunda. Güncelleme yayınlarken `app/build.gradle.kts` içinde bu sayıyı artırın.
