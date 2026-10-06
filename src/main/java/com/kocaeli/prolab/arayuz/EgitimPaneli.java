package com.kocaeli.prolab.arayuz;

import com.kocaeli.prolab.degerlendirme.PerformansOlcer;
import com.kocaeli.prolab.siniflandirici.KNNSiniflandirici;
import com.kocaeli.prolab.siniflandirici.KararAgaciSiniflandirici;
import com.kocaeli.prolab.siniflandirici.TemelAlgoritma;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

// 2. Sekme: Model Egitimi
// Algoritma ve parametre secilir, egitim + test arka planda yapilir.
public class EgitimPaneli extends JPanel {

    private UygulamaDurumu durum;

    private JRadioButton knnSecimi;
    private JRadioButton kararAgaciSecimi;

    private JSpinner kDegeriSpinner;
    private JSpinner maksimumDerinlikSpinner;
    private JSpinner minKayitSayisiSpinner;

    private JButton egitButonu;
    private JTextArea logAlani;

    public EgitimPaneli(UygulamaDurumu durum) {
        this.durum = durum;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(algoritmaSeciciPanel(), BorderLayout.NORTH);
        add(parametrePanel(), BorderLayout.CENTER);
        add(altPanel(), BorderLayout.SOUTH);

        knnSecimi.setSelected(true);
        parametreGoster();
    }

    private JPanel algoritmaSeciciPanel() {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBorder(new TitledBorder("Algoritma Secimi"));

        knnSecimi = new JRadioButton("K-En Yakin Komsu (KNN)");
        kararAgaciSecimi = new JRadioButton("Karar Agaci (Decision Tree)");

        ButtonGroup grup = new ButtonGroup();
        grup.add(knnSecimi);
        grup.add(kararAgaciSecimi);

        knnSecimi.addActionListener(e -> parametreGoster());
        kararAgaciSecimi.addActionListener(e -> parametreGoster());

        p.add(knnSecimi);
        p.add(kararAgaciSecimi);
        return p;
    }

    private JPanel parametrePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new TitledBorder("Parametreler"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("K degeri (KNN):"), gbc);
        kDegeriSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        kDegeriSpinner.setPreferredSize(new Dimension(80, 25));
        gbc.gridx = 1;
        p.add(kDegeriSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        p.add(new JLabel("Maksimum derinlik (Karar Agaci):"), gbc);
        maksimumDerinlikSpinner = new JSpinner(new SpinnerNumberModel(8, 1, 30, 1));
        maksimumDerinlikSpinner.setPreferredSize(new Dimension(80, 25));
        gbc.gridx = 1;
        p.add(maksimumDerinlikSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        p.add(new JLabel("Yaprakta min kayit (Karar Agaci):"), gbc);
        minKayitSayisiSpinner = new JSpinner(new SpinnerNumberModel(5, 2, 50, 1));
        minKayitSayisiSpinner.setPreferredSize(new Dimension(80, 25));
        gbc.gridx = 1;
        p.add(minKayitSayisiSpinner, gbc);

        return p;
    }

    private JPanel altPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));

        egitButonu = new JButton("Modeli Egit ve Test Et");
        egitButonu.setFont(egitButonu.getFont().deriveFont(Font.BOLD, 13f));
        egitButonu.addActionListener(e -> modeliCalistir());

        JPanel butonPanel = new JPanel();
        butonPanel.add(egitButonu);
        p.add(butonPanel, BorderLayout.NORTH);

        logAlani = new JTextArea(10, 50);
        logAlani.setEditable(false);
        logAlani.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane kaydir = new JScrollPane(logAlani);
        kaydir.setBorder(new TitledBorder("Egitim Log"));
        p.add(kaydir, BorderLayout.CENTER);

        return p;
    }

    private void parametreGoster() {
        boolean knnMi = knnSecimi.isSelected();
        kDegeriSpinner.setEnabled(knnMi);
        maksimumDerinlikSpinner.setEnabled(!knnMi);
        minKayitSayisiSpinner.setEnabled(!knnMi);
    }

    private void modeliCalistir() {
        logAlani.setText("");

        if (!durum.veriYuklendiMi()) {
            logAlani.setText("HATA: Once 1. sekmeden veri yuklemelisiniz.");
            return;
        }

        // Polymorphism: secime gore KNN veya Karar Agaci, ikisi de TemelAlgoritma
        TemelAlgoritma algo;
        if (knnSecimi.isSelected()) {
            int k = (Integer) kDegeriSpinner.getValue();
            algo = new KNNSiniflandirici(k);
        } else {
            int derinlik = (Integer) maksimumDerinlikSpinner.getValue();
            int minKayit = (Integer) minKayitSayisiSpinner.getValue();
            algo = new KararAgaciSiniflandirici(derinlik, minKayit);
        }

        yaz("Algoritma: " + algo.algoritmaAdi());
        yaz("Egitim kayit sayisi: " + durum.getEgitimKayitlari().size());
        yaz("Test kayit sayisi: " + durum.getTestKayitlari().size());
        yaz("");
        yaz("Egitim basladi...");

        // Arka planda calistir ki pencere donmasin
        egitButonu.setEnabled(false);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            double basari;
            Map<String, Map<String, Integer>> matris;

            @Override
            protected Void doInBackground() {
                PerformansOlcer olcer = new PerformansOlcer();

                algo.egitZamanli(durum.getEgitimKayitlari());
                basari = algo.basariOraniHesapla(durum.getTestKayitlari());
                matris = olcer.confusionMatrixOlustur(algo, durum.getTestKayitlari());

                return null;
            }

            @Override
            protected void done() {
                durum.setSonAlgoritma(algo);
                durum.setSonBasariOrani(basari);
                durum.setSonConfusionMatrix(matris);

                yaz("Egitim tamamlandi.");
                yaz("Egitim suresi: " + algo.getSonEgitimSuresiMs() + " ms");
                yaz("Tahmin suresi (toplam): " + algo.getSonTahminSuresiMs() + " ms");
                yaz(String.format("Basari orani: %.2f%%", basari));
                yaz("");
                yaz("Detayli sonuclar 3. sekmede!");

                egitButonu.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void yaz(String satir) {
        logAlani.append(satir + "\n");
    }
}
