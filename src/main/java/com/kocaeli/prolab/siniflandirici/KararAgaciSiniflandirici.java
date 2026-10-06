package com.kocaeli.prolab.siniflandirici;

import com.kocaeli.prolab.model.SatisKaydi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Karar Agaci: veriyi Information Gain'e gore ozyineli olarak bolerek kural agaci kurar.
public class KararAgaciSiniflandirici extends TemelAlgoritma {

    private Dugum kokDugum;

    private int maksimumDerinlik;

    private int minKayitSayisi;

    public KararAgaciSiniflandirici(int maksimumDerinlik, int minKayitSayisi) {
        super();
        this.maksimumDerinlik = maksimumDerinlik;
        this.minKayitSayisi = minKayitSayisi;
    }

    @Override
    public void modeliEgit(List<SatisKaydi> egitimVerisi) {
        this.kokDugum = agacInsaEt(new ArrayList<>(egitimVerisi), 0);
    }

    // Kokten baslayip yapraga kadar agacta gezinir
    @Override
    public String tahminEt(SatisKaydi gelenKayit) {
        Dugum simdikiDugum = kokDugum;

        while (!simdikiDugum.isYaprakMi()) {
            double kayitDegeri = ozellikDegeriAl(gelenKayit, simdikiDugum.getOzellikIndeksi());

            if (kayitDegeri <= simdikiDugum.getEsikDegeri()) {
                simdikiDugum = simdikiDugum.getSolDal();
            } else {
                simdikiDugum = simdikiDugum.getSagDal();
            }
        }

        return simdikiDugum.getTahminKategori();
    }

    @Override
    public String algoritmaAdi() {
        return "Karar Agaci (derinlik=" + maksimumDerinlik + ")";
    }

    // Ozyineli agac kurma
    private Dugum agacInsaEt(List<SatisKaydi> kayitlar, int simdikiDerinlik) {

        // Durdurma kriterleri
        if (kayitlar.size() < minKayitSayisi ||
                simdikiDerinlik >= maksimumDerinlik ||
                hepsiAyniKategoriMi(kayitlar)) {
            String cogunlukKategori = cogunlukKategoriBul(kayitlar);
            return Dugum.yaprakOlustur(cogunlukKategori);
        }

        EnIyiBolme enIyi = enIyiBolmeyiBul(kayitlar);

        if (enIyi == null || enIyi.bilgiKazanci <= 0) {
            String cogunlukKategori = cogunlukKategoriBul(kayitlar);
            return Dugum.yaprakOlustur(cogunlukKategori);
        }

        List<SatisKaydi> solKayitlar = new ArrayList<>();
        List<SatisKaydi> sagKayitlar = new ArrayList<>();

        for (SatisKaydi k : kayitlar) {
            double deger = ozellikDegeriAl(k, enIyi.ozellikIndeksi);
            if (deger <= enIyi.esikDegeri) {
                solKayitlar.add(k);
            } else {
                sagKayitlar.add(k);
            }
        }

        if (solKayitlar.isEmpty() || sagKayitlar.isEmpty()) {
            String cogunlukKategori = cogunlukKategoriBul(kayitlar);
            return Dugum.yaprakOlustur(cogunlukKategori);
        }

        // Recursive: sol ve sag alt agaclari kur
        Dugum solDal = agacInsaEt(solKayitlar, simdikiDerinlik + 1);
        Dugum sagDal = agacInsaEt(sagKayitlar, simdikiDerinlik + 1);

        return Dugum.icDugumOlustur(enIyi.ozellikIndeksi, enIyi.esikDegeri, solDal, sagDal);
    }

    // En yuksek Information Gain'i veren ozellik-esik ikilisini bulur
    private EnIyiBolme enIyiBolmeyiBul(List<SatisKaydi> kayitlar) {
        double anaEntropi = entropiHesapla(kayitlar);
        EnIyiBolme enIyi = null;

        for (int ozellik = 0; ozellik < 3; ozellik++) {
            List<Double> adayEsikler = adayEsikleriTopla(kayitlar, ozellik);

            for (double esik : adayEsikler) {
                List<SatisKaydi> sol = new ArrayList<>();
                List<SatisKaydi> sag = new ArrayList<>();
                for (SatisKaydi k : kayitlar) {
                    if (ozellikDegeriAl(k, ozellik) <= esik) sol.add(k);
                    else sag.add(k);
                }

                if (sol.isEmpty() || sag.isEmpty()) continue;

                double agirlikliEntropi =
                        ((double) sol.size() / kayitlar.size()) * entropiHesapla(sol) +
                                ((double) sag.size() / kayitlar.size()) * entropiHesapla(sag);
                double bilgiKazanci = anaEntropi - agirlikliEntropi;

                if (enIyi == null || bilgiKazanci > enIyi.bilgiKazanci) {
                    enIyi = new EnIyiBolme(ozellik, esik, bilgiKazanci);
                }
            }
        }

        return enIyi;
    }

    // Komsu farkli degerlerin ortalamasini aday esik yapar, en fazla ~20 aday
    private List<Double> adayEsikleriTopla(List<SatisKaydi> kayitlar, int ozellik) {
        List<Double> degerler = new ArrayList<>();
        for (SatisKaydi k : kayitlar) {
            degerler.add(ozellikDegeriAl(k, ozellik));
        }
        Collections.sort(degerler);

        List<Double> esikler = new ArrayList<>();
        for (int i = 1; i < degerler.size(); i++) {
            if (!degerler.get(i).equals(degerler.get(i - 1))) {
                double ortalama = (degerler.get(i) + degerler.get(i - 1)) / 2.0;
                esikler.add(ortalama);
            }
        }

        if (esikler.size() > 20) {
            List<Double> orneklenmis = new ArrayList<>();
            int adim = esikler.size() / 20;
            for (int i = 0; i < esikler.size(); i += adim) {
                orneklenmis.add(esikler.get(i));
            }
            return orneklenmis;
        }

        return esikler;
    }

    // H(S) = -sum p(i) * log2(p(i))
    private double entropiHesapla(List<SatisKaydi> kayitlar) {
        if (kayitlar.isEmpty()) return 0.0;

        HashMap<String, Integer> sayilar = new HashMap<>();
        for (SatisKaydi k : kayitlar) {
            sayilar.merge(k.getKategori(), 1, Integer::sum);
        }

        double entropi = 0.0;
        int toplam = kayitlar.size();
        for (int sayi : sayilar.values()) {
            double oran = (double) sayi / toplam;
            if (oran > 0) {
                entropi -= oran * (Math.log(oran) / Math.log(2));
            }
        }

        return entropi;
    }

    private double ozellikDegeriAl(SatisKaydi kayit, int indeks) {
        switch (indeks) {
            case 0: return kayit.getCinsiyetKodu();
            case 1: return kayit.getTutarNormalize();
            case 2: return kayit.getMarkaKoduNormalize();
            default: throw new IllegalArgumentException("Gecersiz ozellik indeksi: " + indeks);
        }
    }

    private boolean hepsiAyniKategoriMi(List<SatisKaydi> kayitlar) {
        if (kayitlar.isEmpty()) return true;
        String ilkKategori = kayitlar.get(0).getKategori();
        for (SatisKaydi k : kayitlar) {
            if (!k.getKategori().equals(ilkKategori)) return false;
        }
        return true;
    }

    private String cogunlukKategoriBul(List<SatisKaydi> kayitlar) {
        HashMap<String, Integer> sayilar = new HashMap<>();
        for (SatisKaydi k : kayitlar) {
            sayilar.merge(k.getKategori(), 1, Integer::sum);
        }

        String enCok = null;
        int enYuksek = -1;
        for (Map.Entry<String, Integer> girdi : sayilar.entrySet()) {
            if (girdi.getValue() > enYuksek) {
                enYuksek = girdi.getValue();
                enCok = girdi.getKey();
            }
        }
        return enCok;
    }

    // En iyi bolmenin bilgilerini bir arada tutan yardimci sinif
    private static class EnIyiBolme {
        int ozellikIndeksi;
        double esikDegeri;
        double bilgiKazanci;

        EnIyiBolme(int ozellikIndeksi, double esikDegeri, double bilgiKazanci) {
            this.ozellikIndeksi = ozellikIndeksi;
            this.esikDegeri = esikDegeri;
            this.bilgiKazanci = bilgiKazanci;
        }
    }
}
