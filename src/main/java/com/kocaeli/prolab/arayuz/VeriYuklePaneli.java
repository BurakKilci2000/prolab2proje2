package com.kocaeli.prolab.arayuz;

import com.kocaeli.prolab.model.SatisKaydi;
import com.kocaeli.prolab.siniflandirici.KNNSiniflandirici;
import com.kocaeli.prolab.veri.VeriHazirlayici;
import com.kocaeli.prolab.veri.VeriYukleyici;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// 1. Sekme: Veri Yukleme
// A) Tek CSV dosyasi -> program %80/%20 boler
// B) Ayri egitim ve test dosyalari
public class VeriYuklePaneli extends JPanel {

    private UygulamaDurumu durum;

    private JRadioButton modTekDosya;
    private JRadioButton modAyriDosya;

    private JTextField tekDosyaYolu;
    private JButton tekDosyaSec;

    private JTextField egitimDosyaYolu;
    private JTextField testDosyaYolu;
    private JButton egitimDosyaSec;
    private JButton testDosyaSec;

    private JButton yukleButonu;
    private JTextArea sonucAlani;

    public VeriYuklePaneli(UygulamaDurumu durum) {
        this.durum = durum;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(modSeciciPanel(), BorderLayout.NORTH);
        add(dosyaSecimPanel(), BorderLayout.CENTER);
        add(altPanel(), BorderLayout.SOUTH);

        modTekDosya.setSelected(true);
        modDegisiklikGoster();
    }

    private JPanel modSeciciPanel() {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBorder(new TitledBorder("Yukleme Modu"));

        modTekDosya = new JRadioButton("Tek CSV dosyasi (program %80/%20 olarak ayirir)");
        modAyriDosya = new JRadioButton("Ayri egitim ve test dosyalari");

        ButtonGroup grup = new ButtonGroup();
        grup.add(modTekDosya);
        grup.add(modAyriDosya);

        modTekDosya.addActionListener(e -> modDegisiklikGoster());
        modAyriDosya.addActionListener(e -> modDegisiklikGoster());

        p.add(modTekDosya);
        p.add(modAyriDosya);
        return p;
    }

    private JPanel dosyaSecimPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Dosya(lar) Secimi"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("CSV dosyasi:"), gbc);
        tekDosyaYolu = new JTextField(30);
        tekDosyaYolu.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(tekDosyaYolu, gbc);
        tekDosyaSec = new JButton("Sec");
        tekDosyaSec.addActionListener(e -> dosyaSec(tekDosyaYolu));
        gbc.gridx = 2; gbc.weightx = 0;
        p.add(tekDosyaSec, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        p.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        p.add(new JLabel("Egitim dosyasi:"), gbc);
        egitimDosyaYolu = new JTextField(30);
        egitimDosyaYolu.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(egitimDosyaYolu, gbc);
        egitimDosyaSec = new JButton("Sec");
        egitimDosyaSec.addActionListener(e -> dosyaSec(egitimDosyaYolu));
        gbc.gridx = 2; gbc.weightx = 0;
        p.add(egitimDosyaSec, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        p.add(new JLabel("Test dosyasi:"), gbc);
        testDosyaYolu = new JTextField(30);
        testDosyaYolu.setEditable(false);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(testDosyaYolu, gbc);
        testDosyaSec = new JButton("Sec");
        testDosyaSec.addActionListener(e -> dosyaSec(testDosyaYolu));
        gbc.gridx = 2; gbc.weightx = 0;
        p.add(testDosyaSec, gbc);

        return p;
    }

    private JPanel altPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));

        yukleButonu = new JButton("Veriyi Yukle ve On Isleme Uygula");
        yukleButonu.setFont(yukleButonu.getFont().deriveFont(Font.BOLD, 13f));
        yukleButonu.addActionListener(e -> veriyiYukle());

        JPanel butonPanel = new JPanel();
        butonPanel.add(yukleButonu);
        p.add(butonPanel, BorderLayout.NORTH);

        sonucAlani = new JTextArea(10, 50);
        sonucAlani.setEditable(false);
        sonucAlani.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane kaydir = new JScrollPane(sonucAlani);
        kaydir.setBorder(new TitledBorder("Yukleme Sonuclari"));
        p.add(kaydir, BorderLayout.CENTER);

        return p;
    }

    private void dosyaSec(JTextField hedefAlan) {
        JFileChooser secici = new JFileChooser();
        secici.setCurrentDirectory(new File("src/main/resources/veri"));
        int sonuc = secici.showOpenDialog(this);
        if (sonuc == JFileChooser.APPROVE_OPTION) {
            hedefAlan.setText(secici.getSelectedFile().getAbsolutePath());
        }
    }

    private void modDegisiklikGoster() {
        boolean tekMod = modTekDosya.isSelected();
        tekDosyaYolu.setEnabled(tekMod);
        tekDosyaSec.setEnabled(tekMod);
        egitimDosyaYolu.setEnabled(!tekMod);
        egitimDosyaSec.setEnabled(!tekMod);
        testDosyaYolu.setEnabled(!tekMod);
        testDosyaSec.setEnabled(!tekMod);
    }

    private void veriyiYukle() {
        sonucAlani.setText("");
        StringBuilder log = new StringBuilder();

        try {
            VeriYukleyici yukleyici = new VeriYukleyici();

            if (modTekDosya.isSelected()) {
                String yol = tekDosyaYolu.getText().trim();
                if (yol.isEmpty()) {
                    yaziYaz(log, "HATA: Dosya secilmedi.");
                    return;
                }

                ArrayList<SatisKaydi> tumu = yukleyici.csvDosyasiOku(yol);
                yaziYaz(log, "Okunan gecerli kayit: " + tumu.size());
                yaziYaz(log, "Temizlenen satir: " + yukleyici.getAtlananSatirSayisi());

                if (tumu.isEmpty()) {
                    yaziYaz(log, "HATA: Hic gecerli kayit okunmadi.");
                    return;
                }

                VeriHazirlayici hazirlayici = new VeriHazirlayici();
                hazirlayici.veriyiHazirla(tumu);

                // %80/%20 bolme (TemelAlgoritma'daki ortak metot)
                KNNSiniflandirici yardimci = new KNNSiniflandirici(5);
                List<List<SatisKaydi>> bolunmus = yardimci.egitimTestAyir(tumu, 0.80, 42L);

                durum.setTumKayitlar(tumu);
                durum.setEgitimKayitlari(bolunmus.get(0));
                durum.setTestKayitlari(bolunmus.get(1));
                durum.setHazirlayici(hazirlayici);

                yaziYaz(log, "On isleme tamamlandi.");
                yaziYaz(log, "Egitim boyutu: " + durum.getEgitimKayitlari().size());
                yaziYaz(log, "Test boyutu: " + durum.getTestKayitlari().size());

            } else {
                String egitimYol = egitimDosyaYolu.getText().trim();
                String testYol = testDosyaYolu.getText().trim();
                if (egitimYol.isEmpty() || testYol.isEmpty()) {
                    yaziYaz(log, "HATA: Hem egitim hem test dosyasi secilmeli.");
                    return;
                }

                ArrayList<SatisKaydi> egitimKayitlari = yukleyici.csvDosyasiOku(egitimYol);
                int egitimAtlanan = yukleyici.getAtlananSatirSayisi();

                VeriYukleyici testYukleyici = new VeriYukleyici();
                ArrayList<SatisKaydi> testKayitlari = testYukleyici.csvDosyasiOku(testYol);
                int testAtlanan = testYukleyici.getAtlananSatirSayisi();

                yaziYaz(log, "Egitim kayit sayisi: " + egitimKayitlari.size() + " (atlanan: " + egitimAtlanan + ")");
                yaziYaz(log, "Test kayit sayisi: " + testKayitlari.size() + " (atlanan: " + testAtlanan + ")");

                if (egitimKayitlari.isEmpty() || testKayitlari.isEmpty()) {
                    yaziYaz(log, "HATA: Bir dosyada gecerli kayit yok.");
                    return;
                }

                // Ayni encoding/normalizasyon icin iki liste birlikte isleniyor
                ArrayList<SatisKaydi> hepsi = new ArrayList<>(egitimKayitlari);
                hepsi.addAll(testKayitlari);

                VeriHazirlayici hazirlayici = new VeriHazirlayici();
                hazirlayici.veriyiHazirla(hepsi);

                durum.setTumKayitlar(hepsi);
                durum.setEgitimKayitlari(egitimKayitlari);
                durum.setTestKayitlari(testKayitlari);
                durum.setHazirlayici(hazirlayici);

                yaziYaz(log, "On isleme tamamlandi.");
            }

            yaziYaz(log, "");
            yaziYaz(log, "Veri hazir! 2. sekmeye gecerek modeli egitebilirsiniz.");

        } catch (Exception ex) {
            yaziYaz(log, "BEKLENMEYEN HATA: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void yaziYaz(StringBuilder log, String satir) {
        log.append(satir).append("\n");
        sonucAlani.setText(log.toString());
    }
}
