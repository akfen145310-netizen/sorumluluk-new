<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Sorumluluk Takibi

Öğrenci sorumluluk ve karakter gelişimi uygulaması; günlük görevler, ebeveyn onayı, seri (streak) takibi ve elmas ödül sistemi.

## Özellikler

- **Öğrenci Paneli**: Günlük sorumluluk görevlerini tamamlama, elmas kazanma, seri ve rozet takibi.
- **Veli Paneli**: Öğrencinin tamamladığı görevleri doğrulama/onaylama ve gelişimini izleme.
- **Öğretmen Paneli**: Sınıf yönetimi, etkinlik ve katılım kodu/QR oluşturma, toplu onay ve analitik verileri.
- **Akıllı QR & Onboarding**: Standart ZXing QR kodları ile sınıfa ve etkinliğe katılım.
- **Yerel ve Bulut Desteği**: Room veritabanı ile tam çevrimdışı çalışma, opsiyonel Firestore entegrasyonu.

## Kurulum ve Çalıştırma

Detaylı APK derleme ve telefona yükleme adımları için [KURULUM.md](KURULUM.md) dosyasına bakabilirsiniz.
Mimari ve hata düzeltme detayları için [DEGISIKLIKLER.md](DEGISIKLIKLER.md) dosyasını inceleyebilirsiniz.

```bash
gradle :app:assembleDebug
```
Çıktı konumu: `app/build/outputs/apk/debug/app-debug.apk`
