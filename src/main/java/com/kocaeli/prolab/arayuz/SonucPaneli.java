package com.kocaeli.prolab.arayuz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

// 3. Sekme: Sonuclar
// Genel metrikler, confusion matrix tablosu ve kategori bazinda basari.
public class SonucPaneli extends JPanel {

    private UygulamaDurumu durum;
    private JTextArea metrikAlani;
    private JTable confusionTablo;
    private JTable kategoriTablo;

    public SonucPaneli(UygulamaDurumu durum) {
        this.durum = durum;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        metrikAlani = new JTextArea(6, 40);
        metrikAlani.setEditable(false);
        metrikAlani.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane metrikKaydir = new JScrollPane(metrikAlani);
        metrikKaydir.setBorder(new TitledBorder("Genel Metrikler"));
        add(metrikKaydir, BorderLayout.NORTH);

        confusionTablo = new JTable();
        JScrollPane confKaydir = new JScrollPane(confusionTablo);
        confKaydir.setBorder(new TitledBorder("Confusion Matrix (Satir=Gercek, Sutun=Tahmin)"));
        add(confKaydir, BorderLayout.CENTER);

        kategoriTablo = new JTable();
        JScrollPane katKaydir = new JScrollPane(kategoriTablo);
        katKaydir.setBorder(new TitledBorder("Kategori Bazinda Basari"));
        katKaydir.setPreferredSize(new Dimension(0, 200));
        add(katKaydir, BorderLayout.SOUTH);
    }

    // Sekme acildiginda cagrilir
    public void yenile() {
        if (!durum.modelEgitildiMi()) {
            metrikAlani.setText("Henuz model egitilmedi. Once 2. sekmeden egitim yapin.");
            confusionTablo.setModel(new DefaultTableModel());
            kategoriTablo.setModel(new DefaultTableModel());
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Algoritma      : ").append(durum.getSonAlgoritma().algoritmaAdi()).append("\n");
        sb.append(String.format("Basari orani   : %.2f%%\n", durum.getSonBasariOrani()));
        sb.append("Egitim suresi  : ").append(durum.getSonAlgoritma().getSonEgitimSuresiMs()).append(" ms\n");
        sb.append("Tahmin suresi  : ").append(durum.getSonAlgoritma().getSonTahminSuresiMs()).append(" ms (toplam)\n");
        sb.append("Egitim boyutu  : ").append(durum.getEgitimKayitlari().size()).append("\n");
        sb.append("Test boyutu    : ").append(durum.getTestKayitlari().size()).append("\n");
        metrikAlani.setText(sb.toString());
        metrikAlani.setCaretPosition(0);

        confusionTablo.setModel(confusionTabloModeli());
        kategoriTablo.setModel(kategoriTabloModeli());
    }

    private DefaultTableModel confusionTabloModeli() {
        Map<String, Map<String, Integer>> matris = durum.getSonConfusionMatrix();

        TreeSet<String> tumKategoriler = new TreeSet<>();
        for (String gercek : matris.keySet()) {
            tumKategoriler.add(gercek);
            tumKategoriler.addAll(matris.get(gercek).keySet());
        }

        String[] basliklar = new String[tumKategoriler.size() + 1];
        basliklar[0] = "Gercek \\ Tahmin";
        int i = 1;
        for (String kat : tumKategoriler) basliklar[i++] = kat;

        Object[][] veriler = new Object[tumKategoriler.size()][basliklar.length];
        int satirIndeks = 0;
        for (String gercek : tumKategoriler) {
            veriler[satirIndeks][0] = gercek;
            Map<String, Integer> satirMap = matris.getOrDefault(gercek, new HashMap<>());
            int sutunIndeks = 1;
            for (String tahmin : tumKategoriler) {
                veriler[satirIndeks][sutunIndeks++] = satirMap.getOrDefault(tahmin, 0);
            }
            satirIndeks++;
        }

        return new DefaultTableModel(veriler, basliklar) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private DefaultTableModel kategoriTabloModeli() {
        Map<String, Map<String, Integer>> matris = durum.getSonConfusionMatrix();

        String[] basliklar = {"Kategori", "Dogru", "Toplam", "Basari (%)"};
        Object[][] veriler = new Object[matris.size()][4];

        int i = 0;
        for (Map.Entry<String, Map<String, Integer>> girdi : matris.entrySet()) {
            String gercekKategori = girdi.getKey();
            Map<String, Integer> tahminler = girdi.getValue();

            int dogru = tahminler.getOrDefault(gercekKategori, 0);
            int toplam = 0;
            for (int s : tahminler.values()) toplam += s;
            double basari = toplam == 0 ? 0.0 : (dogru * 100.0 / toplam);

            veriler[i][0] = gercekKategori;
            veriler[i][1] = dogru;
            veriler[i][2] = toplam;
            veriler[i][3] = String.format("%.2f", basari);
            i++;
        }

        return new DefaultTableModel(veriler, basliklar) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
    }
}
