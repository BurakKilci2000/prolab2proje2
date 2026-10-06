package com.kocaeli.prolab.siniflandirici;

import com.kocaeli.prolab.model.SatisKaydi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Kalitim: KNN ve Karar Agaci'nin ortak metotlarini toplayan soyut sinif.
public abstract class TemelAlgoritma implements ISiniflandirici {

    protected long sonEgitimSuresiMs;
    protected long sonTahminSuresiMs;

    public TemelAlgoritma() {
        this.sonEgitimSuresiMs = 0;
        this.sonTahminSuresiMs = 0;
    }

    @Override
    public abstract void modeliEgit(List<SatisKaydi> egitimVerisi);

    @Override
    public abstract String tahminEt(SatisKaydi gelenKayit);

    @Override
    public abstract String algoritmaAdi();

    // Accuracy = dogru tahmin / toplam * 100
    public double basariOraniHesapla(List<SatisKaydi> testVerisi) {
        if (testVerisi == null || testVerisi.isEmpty()) {
            return 0.0;
        }

        int dogruTahmin = 0;
        long baslangic = System.currentTimeMillis();

        for (SatisKaydi kayit : testVerisi) {
            String tahminEdilenKategori = tahminEt(kayit);
            String gercekKategori = kayit.getKategori();

            if (tahminEdilenKategori.equals(gercekKategori)) {
                dogruTahmin++;
            }
        }

        sonTahminSuresiMs = System.currentTimeMillis() - baslangic;

        double basariOrani = (double) dogruTahmin / testVerisi.size() * 100.0;
        return basariOrani;
    }

    // Veriyi karistirip egitim/test olarak ayirir (seed ile tekrarlanabilir)
    public List<List<SatisKaydi>> egitimTestAyir(List<SatisKaydi> tumVeri,
                                                 double egitimOrani,
                                                 long rastgeleTohum) {
        List<SatisKaydi> karisikListe = new ArrayList<>(tumVeri);

        Collections.shuffle(karisikListe, new Random(rastgeleTohum));

        int egitimBoyutu = (int) (karisikListe.size() * egitimOrani);

        List<SatisKaydi> egitim = new ArrayList<>(karisikListe.subList(0, egitimBoyutu));
        List<SatisKaydi> test = new ArrayList<>(karisikListe.subList(egitimBoyutu, karisikListe.size()));

        List<List<SatisKaydi>> sonuc = new ArrayList<>();
        sonuc.add(egitim);
        sonuc.add(test);
        return sonuc;
    }

    // Egitimi sure olcerek yapar
    public void egitZamanli(List<SatisKaydi> egitimVerisi) {
        long baslangic = System.currentTimeMillis();
        modeliEgit(egitimVerisi);
        sonEgitimSuresiMs = System.currentTimeMillis() - baslangic;
    }

    public long getSonEgitimSuresiMs() {
        return sonEgitimSuresiMs;
    }

    public long getSonTahminSuresiMs() {
        return sonTahminSuresiMs;
    }
}
