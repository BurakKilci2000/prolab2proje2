package com.kocaeli.prolab.siniflandirici;

// Karar agacinin tek bir dugumu: ic dugum (soru) veya yaprak (sonuc).
public class Dugum {

    private boolean yaprakMi;

    // Yaprak dugum alani
    private String tahminKategori;

    // Ic dugum alanlari
    private int ozellikIndeksi;   // 0=cinsiyet, 1=tutar, 2=marka
    private double esikDegeri;
    private Dugum solDal;         // deger <= esik
    private Dugum sagDal;         // deger >  esik

    public static Dugum yaprakOlustur(String tahminKategori) {
        Dugum d = new Dugum();
        d.yaprakMi = true;
        d.tahminKategori = tahminKategori;
        return d;
    }

    public static Dugum icDugumOlustur(int ozellikIndeksi, double esikDegeri,
                                       Dugum solDal, Dugum sagDal) {
        Dugum d = new Dugum();
        d.yaprakMi = false;
        d.ozellikIndeksi = ozellikIndeksi;
        d.esikDegeri = esikDegeri;
        d.solDal = solDal;
        d.sagDal = sagDal;
        return d;
    }

    // Disaridan new Dugum() yapilamaz, sadece yukaridaki metotlarla olusturulur
    private Dugum() {}

    public boolean isYaprakMi() { return yaprakMi; }
    public String getTahminKategori() { return tahminKategori; }
    public int getOzellikIndeksi() { return ozellikIndeksi; }
    public double getEsikDegeri() { return esikDegeri; }
    public Dugum getSolDal() { return solDal; }
    public Dugum getSagDal() { return sagDal; }
}
