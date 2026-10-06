package com.kocaeli.prolab.veri;

import com.kocaeli.prolab.model.SatisKaydi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// CSV dosyasini satir satir okuyup her satiri SatisKaydi nesnesine donusturen,
// ayni zamanda eksik ve hatali verileri temizleyen sinif.
public class VeriYukleyici {

    private int atlananSatirSayisi;

    public VeriYukleyici() {
        this.atlananSatirSayisi = 0;
    }

    public ArrayList<SatisKaydi> csvDosyasiOku(String dosyaYolu) {
        ArrayList<SatisKaydi> kayitListesi = new ArrayList<>();

        // try-with-resources: okuyucu blok sonunda otomatik kapanir
        try (BufferedReader okuyucu = new BufferedReader(new FileReader(dosyaYolu))) {

            // Ilk satir baslik, veri degil
            String baslikSatiri = okuyucu.readLine();
            if (baslikSatiri == null) {
                System.out.println("Dosya bos, okuyacak satir yok!");
                return kayitListesi;
            }

            String satir;
            int satirNumarasi = 1;

            while ((satir = okuyucu.readLine()) != null) {
                satirNumarasi++;

                // -1: sondaki bos alanlar da korunsun
                String[] parcalar = satir.split(",", -1);

                // Asama 1: sutun sayisi kontrolu
                if (parcalar.length != 6) {
                    atlananSatirSayisi++;
                    continue;
                }

                // Asama 2: eksik deger kontrolu
                boolean eksikVarMi = false;
                for (String parca : parcalar) {
                    if (parca == null || parca.trim().isEmpty()) {
                        eksikVarMi = true;
                        break;
                    }
                }

                if (eksikVarMi) {
                    atlananSatirSayisi++;
                    continue;
                }

                // Asama 3: tip donusumu hatasi kontrolu
                try {
                    // musteriKodu CSV'de 123696.0 seklinde geliyor
                    int musteriKodu = (int) Double.parseDouble(parcalar[0].trim());
                    String cinsiyet = parcalar[1].trim();
                    double tutar = Double.parseDouble(parcalar[2].trim());
                    String markaKodu = parcalar[3].trim();
                    String marka = parcalar[4].trim();
                    String kategori = parcalar[5].trim();

                    SatisKaydi kayit = new SatisKaydi(
                            musteriKodu, cinsiyet, tutar,
                            markaKodu, marka, kategori
                    );
                    kayitListesi.add(kayit);

                } catch (NumberFormatException hata) {
                    atlananSatirSayisi++;
                }
            }

        } catch (IOException hata) {
            System.out.println("Dosya okunamadi: " + dosyaYolu);
            System.out.println("Hata mesaji: " + hata.getMessage());
        }

        System.out.println("Okunan gecerli satir sayisi: " + kayitListesi.size());
        System.out.println("Temizlenen (atlanan) satir sayisi: " + atlananSatirSayisi);

        return kayitListesi;
    }

    public int getAtlananSatirSayisi() {
        return atlananSatirSayisi;
    }
}
