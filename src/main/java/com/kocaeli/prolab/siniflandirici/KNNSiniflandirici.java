package com.kocaeli.prolab.siniflandirici;

import com.kocaeli.prolab.model.SatisKaydi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

// K-En Yakin Komsu: en yakin K komsunun cogunluk kategorisini tahmin eder.
public class KNNSiniflandirici extends TemelAlgoritma {

    private int kDegeri;

    private List<SatisKaydi> egitimKayitlari;

    public KNNSiniflandirici(int kDegeri) {
        super();
        this.kDegeri = kDegeri;
        this.egitimKayitlari = new ArrayList<>();
    }

    // Tembel ogrenme: sadece veriyi sakla
    @Override
    public void modeliEgit(List<SatisKaydi> egitimVerisi) {
        this.egitimKayitlari = new ArrayList<>(egitimVerisi);
    }

    @Override
    public String tahminEt(SatisKaydi gelenKayit) {
        // Max-heap: tepede en uzak komsu durur
        PriorityQueue<Komsu> enYakinKomsular = new PriorityQueue<>(
                Comparator.comparingDouble(Komsu::getMesafe).reversed()
        );

        for (SatisKaydi egitimKaydi : egitimKayitlari) {
            double mesafe = oklidMesafesi(gelenKayit, egitimKaydi);

            if (enYakinKomsular.size() < kDegeri) {
                enYakinKomsular.offer(new Komsu(egitimKaydi, mesafe));
            } else if (mesafe < enYakinKomsular.peek().getMesafe()) {
                enYakinKomsular.poll();
                enYakinKomsular.offer(new Komsu(egitimKaydi, mesafe));
            }
        }

        // Cogunluk oylamasi
        HashMap<String, Integer> oyTablosu = new HashMap<>();
        for (Komsu komsu : enYakinKomsular) {
            String kategori = komsu.getKayit().getKategori();
            oyTablosu.put(kategori, oyTablosu.getOrDefault(kategori, 0) + 1);
        }

        String kazananKategori = null;
        int enYuksekOy = -1;
        for (Map.Entry<String, Integer> girdi : oyTablosu.entrySet()) {
            if (girdi.getValue() > enYuksekOy) {
                enYuksekOy = girdi.getValue();
                kazananKategori = girdi.getKey();
            }
        }

        return kazananKategori;
    }

    @Override
    public String algoritmaAdi() {
        return "KNN (k=" + kDegeri + ")";
    }

    // sqrt( dCinsiyet^2 + dTutar^2 + dMarka^2 )
    private double oklidMesafesi(SatisKaydi a, SatisKaydi b) {
        double cinsiyetFark = a.getCinsiyetKodu() - b.getCinsiyetKodu();
        double tutarFark    = a.getTutarNormalize() - b.getTutarNormalize();
        double markaFark    = a.getMarkaKoduNormalize() - b.getMarkaKoduNormalize();

        return Math.sqrt(
                cinsiyetFark * cinsiyetFark +
                        tutarFark * tutarFark +
                        markaFark * markaFark
        );
    }

    // Kayit + mesafe bilgisini birlikte tutan yardimci sinif
    private static class Komsu {
        private SatisKaydi kayit;
        private double mesafe;

        public Komsu(SatisKaydi kayit, double mesafe) {
            this.kayit = kayit;
            this.mesafe = mesafe;
        }

        public SatisKaydi getKayit() { return kayit; }
        public double getMesafe() { return mesafe; }
    }
}
