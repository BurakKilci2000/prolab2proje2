package com.kocaeli.prolab.veri;

import com.kocaeli.prolab.model.SatisKaydi;

import java.util.ArrayList;
import java.util.HashMap;

// Ham veri listesini alip metin alanlarini sayiya ceviren (encoding)
// ve sayisal alanlari [0,1] arasina ceken (normalizasyon) sinif.
public class VeriHazirlayici {

    // Marka kodu -> sayi eslemesi (ayni marka her zaman ayni sayiyi alir)
    private HashMap<String, Integer> markaEslemeSozlugu;

    // Min-Max icin saklanan degerler
    private double tutarMinimum;
    private double tutarMaksimum;

    public VeriHazirlayici() {
        this.markaEslemeSozlugu = new HashMap<>();
        this.tutarMinimum = 0.0;
        this.tutarMaksimum = 0.0;
    }

    // Tek dugme: tum on isleme adimlarini sirayla uygular
    public void veriyiHazirla(ArrayList<SatisKaydi> kayitlar) {
        cinsiyetKodla(kayitlar);
        markaKodla(kayitlar);
        markaNormalizeEt(kayitlar);
        tutarNormalizeEt(kayitlar);
        System.out.println("Veri on isleme tamamlandi.");
    }

    // K -> 0, E -> 1
    private void cinsiyetKodla(ArrayList<SatisKaydi> kayitlar) {
        for (SatisKaydi kayit : kayitlar) {
            String cinsiyet = kayit.getCinsiyet();
            if (cinsiyet.equals("K")) {
                kayit.setCinsiyetKodu(0);
            } else if (cinsiyet.equals("E")) {
                kayit.setCinsiyetKodu(1);
            } else {
                kayit.setCinsiyetKodu(-1);
            }
        }
    }

    // Her benzersiz marka koduna 0'dan baslayan bir sayi atar
    private void markaKodla(ArrayList<SatisKaydi> kayitlar) {
        int siradakiKod = 0;

        for (SatisKaydi kayit : kayitlar) {
            String markaKodu = kayit.getMarkaKodu();

            if (!markaEslemeSozlugu.containsKey(markaKodu)) {
                markaEslemeSozlugu.put(markaKodu, siradakiKod);
                siradakiKod++;
            }

            int sayisalDeger = markaEslemeSozlugu.get(markaKodu);
            kayit.setMarkaKoduSayisal(sayisalDeger);
        }

        System.out.println("Toplam benzersiz marka sayisi: " + markaEslemeSozlugu.size());
    }

    // Min-Max: (x - min) / (max - min)
    private void tutarNormalizeEt(ArrayList<SatisKaydi> kayitlar) {
        if (kayitlar.isEmpty()) {
            return;
        }

        tutarMinimum = kayitlar.get(0).getTutar();
        tutarMaksimum = kayitlar.get(0).getTutar();

        for (SatisKaydi kayit : kayitlar) {
            double tutar = kayit.getTutar();
            if (tutar < tutarMinimum) {
                tutarMinimum = tutar;
            }
            if (tutar > tutarMaksimum) {
                tutarMaksimum = tutar;
            }
        }

        // Sifira bolme kontrolu
        if (tutarMaksimum == tutarMinimum) {
            for (SatisKaydi kayit : kayitlar) {
                kayit.setTutarNormalize(0.0);
            }
            return;
        }

        for (SatisKaydi kayit : kayitlar) {
            double normalize = (kayit.getTutar() - tutarMinimum)
                    / (tutarMaksimum - tutarMinimum);
            kayit.setTutarNormalize(normalize);
        }

        System.out.println("Tutar araligi: [" + tutarMinimum + ", " + tutarMaksimum + "]");
    }

    // Marka kodu 0..(size-1) arasinda; size-1'e bolerek [0,1]'e cekiyoruz
    private void markaNormalizeEt(ArrayList<SatisKaydi> kayitlar) {
        if (kayitlar.isEmpty()) return;

        double maksDeger = markaEslemeSozlugu.size() - 1;

        if (maksDeger <= 0) {
            for (SatisKaydi kayit : kayitlar) {
                kayit.setMarkaKoduNormalize(0.0);
            }
            return;
        }

        for (SatisKaydi kayit : kayitlar) {
            double normalize = kayit.getMarkaKoduSayisal() / maksDeger;
            kayit.setMarkaKoduNormalize(normalize);
        }
    }

    public HashMap<String, Integer> getMarkaEslemeSozlugu() {
        return markaEslemeSozlugu;
    }

    public double getTutarMinimum() {
        return tutarMinimum;
    }

    public double getTutarMaksimum() {
        return tutarMaksimum;
    }
}
