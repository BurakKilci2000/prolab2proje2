package com.kocaeli.prolab;

import com.kocaeli.prolab.degerlendirme.PerformansOlcer;
import com.kocaeli.prolab.model.SatisKaydi;
import com.kocaeli.prolab.siniflandirici.KNNSiniflandirici;
import com.kocaeli.prolab.siniflandirici.KararAgaciSiniflandirici;
import com.kocaeli.prolab.siniflandirici.TemelAlgoritma;
import com.kocaeli.prolab.veri.VeriHazirlayici;
import com.kocaeli.prolab.veri.VeriYukleyici;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Konsol uzerinden toplu deney calistirma (arayuz icin AnaEkran'i calistirin)
public class Main {
    public static void main(String[] args) {
        // 1) Veri yukleme
        VeriYukleyici yukleyici = new VeriYukleyici();
        ArrayList<SatisKaydi> kayitlar = yukleyici.csvDosyasiOku(
                "src/main/resources/veri/MarketSalesKocaeli.csv"
        );

        // 2) On isleme
        VeriHazirlayici hazirlayici = new VeriHazirlayici();
        hazirlayici.veriyiHazirla(kayitlar);

        // 3) Egitim-test ayirma (%80 / %20, seed=42)
        KNNSiniflandirici gecici = new KNNSiniflandirici(5);
        List<List<SatisKaydi>> bolunmus = gecici.egitimTestAyir(kayitlar, 0.80, 42L);
        List<SatisKaydi> egitim = bolunmus.get(0);
        List<SatisKaydi> test = bolunmus.get(1);

        PerformansOlcer olcer = new PerformansOlcer();

        // 4) Algoritma kiyaslama (polymorphism)
        List<TemelAlgoritma> algoritmalar = Arrays.asList(
                new KNNSiniflandirici(3),
                new KNNSiniflandirici(5),
                new KNNSiniflandirici(7),
                new KNNSiniflandirici(9),
                new KararAgaciSiniflandirici(5, 5),
                new KararAgaciSiniflandirici(8, 5),
                new KararAgaciSiniflandirici(12, 5)
        );
        olcer.algoritmalariKiyasla(algoritmalar, egitim, test);

        // 5) Detayli raporlar
        olcer.tekAlgoritmaRaporu(new KNNSiniflandirici(5), egitim, test);
        olcer.tekAlgoritmaRaporu(new KararAgaciSiniflandirici(8, 5), egitim, test);
    }
}
