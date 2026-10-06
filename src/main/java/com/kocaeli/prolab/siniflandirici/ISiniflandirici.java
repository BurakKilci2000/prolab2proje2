package com.kocaeli.prolab.siniflandirici;

import com.kocaeli.prolab.model.SatisKaydi;

import java.util.List;

// Soyutlama: KNN ve Karar Agaci'nin uymak zorunda oldugu sozlesme.
public interface ISiniflandirici {

    // Algoritmanin ogrenme asamasi
    void modeliEgit(List<SatisKaydi> egitimVerisi);

    // Tek bir kayit icin kategori tahmini
    String tahminEt(SatisKaydi gelenKayit);

    // Raporlamada kullanilan algoritma adi
    String algoritmaAdi();
}
