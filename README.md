# KNN ve Karar Ağacı Algoritmalarının Karşılaştırmalı Analizi

Kocaeli Üniversitesi Bilgisayar Mühendisliği — Programlama Laboratuvarı II, Proje II

Kocaeli'ye ait gerçek bir perakende satış veri kümesi (Mart 2017) üzerinde, müşterinin cinsiyeti, harcama tutarı ve aldığı markaya bakarak **ürün kategorisini tahmin eden** iki makine öğrenmesi algoritması, **hiçbir hazır ML kütüphanesi kullanılmadan** Java ile sıfırdan geliştirilmiştir. Proje Nesne Yönelimli Programlama (kapsülleme, soyutlama, kalıtım, çok biçimlilik) prensipleri üzerine kuruludur.

## Sonuçlar

| Algoritma | Doğruluk | Eğitim | Tahmin (925 kayıt) |
|---|---|---|---|
| KNN (k=3) | %77.41 | 0 ms | ~130 ms |
| KNN (k=5) | %74.59 | 0 ms | ~125 ms |
| Karar Ağacı (derinlik=8) | %80.22 | ~110 ms | ~1 ms |
| **Karar Ağacı (derinlik=12)** | **%86.27** | ~110 ms | ~1 ms |

Karar Ağacı hem daha doğru hem de tahminde yaklaşık 130 kat daha hızlı çalışmıştır.

## Veri Seti

- Ham veri: 5094 satış kaydı
- Temizleme sonrası: 4625 kayıt (eksik/hatalı 469 satır atıldı)
- Eğitim / test: %80 (3700) / %20 (925), seed = 42
- Özellikler: cinsiyet, tutar (LINENETTOTAL), marka kodu
- Hedef: CATEGORY_NAME1 (11 kategori)

## Proje Yapısı

```
src/main/java/com/kocaeli/prolab/
├── model/           SatisKaydi                 (kapsülleme)
├── veri/            VeriYukleyici, VeriHazirlayici (okuma, temizleme, encoding, normalizasyon)
├── siniflandirici/  ISiniflandirici            (interface - soyutlama)
│                    TemelAlgoritma             (abstract - kalıtım)
│                    KNNSiniflandirici          (Öklid mesafesi + PriorityQueue)
│                    KararAgaciSiniflandirici   (recursive + Information Gain)
│                    Dugum
├── degerlendirme/   PerformansOlcer            (accuracy, confusion matrix)
├── arayuz/          AnaEkran + 4 sekme paneli  (Java Swing)
└── Main.java        konsol üzerinden toplu deney
```

## Çalıştırma

Gereksinim: JDK 17 veya üzeri. Dış bağımlılık yoktur.

1. Projeyi IntelliJ IDEA ile açın (Maven projesi olarak tanınır).
2. **Arayüz için:** `arayuz/AnaEkran.java` → Run
   - 1. sekmeden `src/main/resources/veri/MarketSalesKocaeli.csv` dosyasını yükleyin
   - 2. sekmeden algoritma ve parametre seçip eğitin
   - 3. ve 4. sekmelerde sonuçları ve grafikleri görün
3. **Konsol deneyi için:** `Main.java` → Run

## Dokümanlar

`docs/` klasöründe IEEE formatında proje raporu (`rapor.pdf`, `rapor.tex`), proje dokümanı ve sunum görselleri bulunmaktadır.

## Geliştirenler

- Burak Kılcı
- Ömer Faruk Teke
