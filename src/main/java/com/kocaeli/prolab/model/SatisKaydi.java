package com.kocaeli.prolab.model;

// Veri setindeki her satiri (bir musterinin bir alisverisini) temsil eden sinif.
// Kapsulleme: tum alanlar private, erisim getter ile, ham alanlara setter yok.
public class SatisKaydi {

    // Ham veri (CSV'den gelir, constructor ile atanir)
    private int musteriKodu;
    private String cinsiyet;
    private double tutar;
    private String markaKodu;
    private String marka;
    private String kategori;

    // Hesaplanmis veri (VeriHazirlayici doldurur)
    private int cinsiyetKodu;
    private int markaKoduSayisal;
    private double tutarNormalize;
    private double markaKoduNormalize;

    public SatisKaydi(int musteriKodu, String cinsiyet, double tutar,
                      String markaKodu, String marka, String kategori) {
        this.musteriKodu = musteriKodu;
        this.cinsiyet = cinsiyet;
        this.tutar = tutar;
        this.markaKodu = markaKodu;
        this.marka = marka;
        this.kategori = kategori;
    }

    public int getMusteriKodu() {
        return musteriKodu;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public double getTutar() {
        return tutar;
    }

    public String getMarkaKodu() {
        return markaKodu;
    }

    public String getMarka() {
        return marka;
    }

    public String getKategori() {
        return kategori;
    }

    public int getCinsiyetKodu() {
        return cinsiyetKodu;
    }

    public void setCinsiyetKodu(int cinsiyetKodu) {
        this.cinsiyetKodu = cinsiyetKodu;
    }

    public int getMarkaKoduSayisal() {
        return markaKoduSayisal;
    }

    public void setMarkaKoduSayisal(int markaKoduSayisal) {
        this.markaKoduSayisal = markaKoduSayisal;
    }

    public double getTutarNormalize() {
        return tutarNormalize;
    }

    public void setTutarNormalize(double tutarNormalize) {
        this.tutarNormalize = tutarNormalize;
    }

    public double getMarkaKoduNormalize() {
        return markaKoduNormalize;
    }

    public void setMarkaKoduNormalize(double markaKoduNormalize) {
        this.markaKoduNormalize = markaKoduNormalize;
    }

    @Override
    public String toString() {
        return "SatisKaydi{" +
                "musteri=" + musteriKodu +
                ", cinsiyet='" + cinsiyet + '\'' +
                ", tutar=" + tutar +
                ", marka='" + marka + '\'' +
                ", kategori='" + kategori + '\'' +
                '}';
    }
}
