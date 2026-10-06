package com.kocaeli.prolab.degerlendirme;

import com.kocaeli.prolab.model.SatisKaydi;
import com.kocaeli.prolab.siniflandirici.ISiniflandirici;
import com.kocaeli.prolab.siniflandirici.TemelAlgoritma;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

// Algoritmalarin performansini olcen sinif: accuracy, confusion matrix,
// kategori bazinda basari ve algoritma kiyaslamasi. Hazir kutuphane yok.
public class PerformansOlcer {

    public void tekAlgoritmaRaporu(TemelAlgoritma algo,
                                   List<SatisKaydi> egitim,
                                   List<SatisKaydi> test) {
        System.out.println("\n================================================");
        System.out.println("  " + algo.algoritmaAdi() + " DEGERLENDIRMESI");
        System.out.println("================================================");

        algo.egitZamanli(egitim);
        double genelBasari = algo.basariOraniHesapla(test);

        System.out.println("Egitim verisi boyutu : " + egitim.size());
        System.out.println("Test verisi boyutu   : " + test.size());
        System.out.println("Egitim suresi        : " + algo.getSonEgitimSuresiMs() + " ms");
        System.out.println("Tahmin suresi (toplam): " + algo.getSonTahminSuresiMs() + " ms");
        System.out.printf ("Genel dogruluk       : %.2f%%\n", genelBasari);

        Map<String, Map<String, Integer>> matris = confusionMatrixOlustur(algo, test);
        confusionMatrixYazdir(matris);

        kategoriBaziBasariYazdir(matris);
    }

    public void algoritmalariKiyasla(List<TemelAlgoritma> algoritmalar,
                                     List<SatisKaydi> egitim,
                                     List<SatisKaydi> test) {
        System.out.println("\n================================================");
        System.out.println("  ALGORITMALAR KIYASLAMASI");
        System.out.println("================================================");
        System.out.printf("%-30s %-15s %-15s %-15s\n",
                "Algoritma", "Dogruluk (%)", "Egitim (ms)", "Tahmin (ms)");
        System.out.println("------------------------------------------------------------------------");

        // Polymorphism: ayni dongude hem KNN hem Karar Agaci calisiyor
        for (TemelAlgoritma algo : algoritmalar) {
            algo.egitZamanli(egitim);
            double basari = algo.basariOraniHesapla(test);
            System.out.printf("%-30s %-15.2f %-15d %-15d\n",
                    algo.algoritmaAdi(),
                    basari,
                    algo.getSonEgitimSuresiMs(),
                    algo.getSonTahminSuresiMs());
        }
    }

    // Dis key = gercek kategori, ic key = tahmin edilen kategori, deger = adet
    public Map<String, Map<String, Integer>> confusionMatrixOlustur(ISiniflandirici algo,
                                                                    List<SatisKaydi> test) {
        Map<String, Map<String, Integer>> matris = new HashMap<>();

        for (SatisKaydi kayit : test) {
            String gercek = kayit.getKategori();
            String tahmin = algo.tahminEt(kayit);

            matris.putIfAbsent(gercek, new HashMap<>());
            Map<String, Integer> satirMap = matris.get(gercek);
            satirMap.merge(tahmin, 1, Integer::sum);
        }

        return matris;
    }

    public void confusionMatrixYazdir(Map<String, Map<String, Integer>> matris) {
        TreeSet<String> tumKategoriler = new TreeSet<>();
        for (String gercek : matris.keySet()) {
            tumKategoriler.add(gercek);
            tumKategoriler.addAll(matris.get(gercek).keySet());
        }

        System.out.println("\n--- Confusion Matrix (Hata Matrisi) ---");
        System.out.println("Satirlar: Gercek kategori | Sutunlar: Tahmin edilen");
        System.out.println();

        System.out.printf("%-20s", "");
        for (String kat : tumKategoriler) {
            System.out.printf("%8s", kisaltmaYap(kat));
        }
        System.out.println();
        System.out.println("-".repeat(20 + tumKategoriler.size() * 8));

        for (String gercek : tumKategoriler) {
            System.out.printf("%-20s", kisaltmaYap(gercek));
            Map<String, Integer> satir = matris.getOrDefault(gercek, new HashMap<>());
            for (String tahmin : tumKategoriler) {
                int sayi = satir.getOrDefault(tahmin, 0);
                System.out.printf("%8d", sayi);
            }
            System.out.println();
        }
    }

    public void kategoriBaziBasariYazdir(Map<String, Map<String, Integer>> matris) {
        System.out.println("\n--- Kategori Bazinda Basari ---");
        System.out.printf("%-25s %-10s %-10s %-15s\n", "Kategori", "Dogru", "Toplam", "Basari (%)");
        System.out.println("-".repeat(65));

        List<KategoriSatiri> satirlar = new ArrayList<>();

        for (Map.Entry<String, Map<String, Integer>> girdi : matris.entrySet()) {
            String gercekKategori = girdi.getKey();
            Map<String, Integer> tahminler = girdi.getValue();

            int dogru = tahminler.getOrDefault(gercekKategori, 0);
            int toplam = 0;
            for (int s : tahminler.values()) toplam += s;

            double basari = toplam == 0 ? 0.0 : (dogru * 100.0 / toplam);
            satirlar.add(new KategoriSatiri(gercekKategori, dogru, toplam, basari));
        }

        // Basariya gore buyukten kucuge
        satirlar.sort(Comparator.comparingDouble(
                (KategoriSatiri s) -> s.basari).reversed());

        for (KategoriSatiri satir : satirlar) {
            System.out.printf("%-25s %-10d %-10d %-15.2f\n",
                    satir.kategori, satir.dogru, satir.toplam, satir.basari);
        }
    }

    // Uzun kategori isimlerini tablo hizalansin diye kisaltir
    private String kisaltmaYap(String kategori) {
        if (kategori.length() <= 7) return kategori;
        return kategori.substring(0, 6) + ".";
    }

    private static class KategoriSatiri {
        String kategori;
        int dogru;
        int toplam;
        double basari;

        KategoriSatiri(String kategori, int dogru, int toplam, double basari) {
            this.kategori = kategori;
            this.dogru = dogru;
            this.toplam = toplam;
            this.basari = basari;
        }
    }
}
