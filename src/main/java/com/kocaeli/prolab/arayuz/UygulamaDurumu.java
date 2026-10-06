package com.kocaeli.prolab.arayuz;

import com.kocaeli.prolab.model.SatisKaydi;
import com.kocaeli.prolab.siniflandirici.TemelAlgoritma;
import com.kocaeli.prolab.veri.VeriHazirlayici;

import java.util.List;
import java.util.Map;

// Paneller arasinda paylasilan veriyi tutan sinif.
// VeriYuklePaneli veriyi buraya yazar, EgitimPaneli buradan okur,
// SonucPaneli ve GrafikPaneli sonuclari buradan alip gosterir.
public class UygulamaDurumu {

    // Veri yukleme sonucu
    private List<SatisKaydi> tumKayitlar;
    private List<SatisKaydi> egitimKayitlari;
    private List<SatisKaydi> testKayitlari;
    private VeriHazirlayici hazirlayici;

    // Son egitilen model
    private TemelAlgoritma sonAlgoritma;
    private double sonBasariOrani;
    private Map<String, Map<String, Integer>> sonConfusionMatrix;

    public List<SatisKaydi> getTumKayitlar() { return tumKayitlar; }
    public void setTumKayitlar(List<SatisKaydi> tumKayitlar) { this.tumKayitlar = tumKayitlar; }

    public List<SatisKaydi> getEgitimKayitlari() { return egitimKayitlari; }
    public void setEgitimKayitlari(List<SatisKaydi> egitimKayitlari) { this.egitimKayitlari = egitimKayitlari; }

    public List<SatisKaydi> getTestKayitlari() { return testKayitlari; }
    public void setTestKayitlari(List<SatisKaydi> testKayitlari) { this.testKayitlari = testKayitlari; }

    public VeriHazirlayici getHazirlayici() { return hazirlayici; }
    public void setHazirlayici(VeriHazirlayici hazirlayici) { this.hazirlayici = hazirlayici; }

    public TemelAlgoritma getSonAlgoritma() { return sonAlgoritma; }
    public void setSonAlgoritma(TemelAlgoritma sonAlgoritma) { this.sonAlgoritma = sonAlgoritma; }

    public double getSonBasariOrani() { return sonBasariOrani; }
    public void setSonBasariOrani(double sonBasariOrani) { this.sonBasariOrani = sonBasariOrani; }

    public Map<String, Map<String, Integer>> getSonConfusionMatrix() { return sonConfusionMatrix; }
    public void setSonConfusionMatrix(Map<String, Map<String, Integer>> sonConfusionMatrix) { this.sonConfusionMatrix = sonConfusionMatrix; }

    public boolean veriYuklendiMi() {
        return egitimKayitlari != null && !egitimKayitlari.isEmpty()
                && testKayitlari != null && !testKayitlari.isEmpty();
    }

    public boolean modelEgitildiMi() {
        return sonAlgoritma != null;
    }
}
