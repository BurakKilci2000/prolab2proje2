package com.kocaeli.prolab.arayuz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

// 4. Sekme: Grafikler
// 1) Kategori bazinda basari - bar chart
// 2) Confusion matrix - isi haritasi (heatmap)
// Dis grafik kutuphanesi yok; paintComponent override edilerek Java2D ile ciziliyor.
public class GrafikPaneli extends JPanel {

    private UygulamaDurumu durum;
    private BarChartPaneli barPanel;
    private HeatmapPaneli heatmapPanel;
    private JLabel bilgiEtiketi;

    public GrafikPaneli(UygulamaDurumu durum) {
        this.durum = durum;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        bilgiEtiketi = new JLabel("Henuz model egitilmedi. Once 2. sekmeden egitim yapin.",
                SwingConstants.CENTER);
        bilgiEtiketi.setFont(bilgiEtiketi.getFont().deriveFont(14f));
        add(bilgiEtiketi, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.5);

        barPanel = new BarChartPaneli();
        JScrollPane barKaydir = new JScrollPane(barPanel);
        barKaydir.setBorder(new TitledBorder("Kategori Bazinda Basari (%)"));
        split.setTopComponent(barKaydir);

        heatmapPanel = new HeatmapPaneli();
        JScrollPane heatKaydir = new JScrollPane(heatmapPanel);
        heatKaydir.setBorder(new TitledBorder("Confusion Matrix - Isi Haritasi"));
        split.setBottomComponent(heatKaydir);

        add(split, BorderLayout.CENTER);
    }

    public void yenile() {
        if (!durum.modelEgitildiMi()) {
            bilgiEtiketi.setText("Henuz model egitilmedi. Once 2. sekmeden egitim yapin.");
            barPanel.veriyiGuncelle(null);
            heatmapPanel.veriyiGuncelle(null);
            return;
        }

        bilgiEtiketi.setText("Model: " + durum.getSonAlgoritma().algoritmaAdi()
                + "  |  Basari: " + String.format("%.2f%%", durum.getSonBasariOrani()));

        barPanel.veriyiGuncelle(durum.getSonConfusionMatrix());
        heatmapPanel.veriyiGuncelle(durum.getSonConfusionMatrix());
    }

    // ================= Bar Chart =================
    private static class BarChartPaneli extends JPanel {

        private List<String> kategoriler = new ArrayList<>();
        private List<Double> oranlar = new ArrayList<>();

        BarChartPaneli() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(800, 300));
        }

        void veriyiGuncelle(Map<String, Map<String, Integer>> matris) {
            kategoriler.clear();
            oranlar.clear();

            if (matris == null) {
                repaint();
                return;
            }

            for (Map.Entry<String, Map<String, Integer>> girdi : matris.entrySet()) {
                String kategori = girdi.getKey();
                Map<String, Integer> tahminler = girdi.getValue();
                int dogru = tahminler.getOrDefault(kategori, 0);
                int toplam = 0;
                for (int s : tahminler.values()) toplam += s;
                double oran = toplam == 0 ? 0.0 : (dogru * 100.0 / toplam);
                kategoriler.add(kategori);
                oranlar.add(oran);
            }

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (kategoriler.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int kenarBosluk = 60;
            int altBosluk = 80;
            int genislik = getWidth() - 2 * kenarBosluk;
            int yukseklik = getHeight() - kenarBosluk - altBosluk;

            if (genislik <= 0 || yukseklik <= 0) {
                g2.dispose();
                return;
            }

            int n = kategoriler.size();
            int barGenisligi = Math.max(1, genislik / (n * 2));
            int basla = kenarBosluk;

            // Eksenler
            g2.setColor(Color.BLACK);
            g2.drawLine(kenarBosluk, kenarBosluk + yukseklik,
                    kenarBosluk + genislik, kenarBosluk + yukseklik);
            g2.drawLine(kenarBosluk, kenarBosluk,
                    kenarBosluk, kenarBosluk + yukseklik);

            // Y ekseni yuzdeleri
            g2.setFont(g2.getFont().deriveFont(10f));
            for (int y = 0; y <= 100; y += 25) {
                int yPx = kenarBosluk + yukseklik - (int) (y / 100.0 * yukseklik);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(kenarBosluk, yPx, kenarBosluk + genislik, yPx);
                g2.setColor(Color.BLACK);
                g2.drawString(y + "%", kenarBosluk - 35, yPx + 4);
            }

            FontMetrics fm = g2.getFontMetrics();
            for (int i = 0; i < n; i++) {
                int xBar = basla + i * barGenisligi * 2 + barGenisligi / 2;
                double oran = oranlar.get(i);
                int barYukseklik = (int) (oran / 100.0 * yukseklik);
                int yBar = kenarBosluk + yukseklik - barYukseklik;

                g2.setColor(oranaGoreRenk(oran));
                g2.fillRect(xBar, yBar, barGenisligi, barYukseklik);

                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(xBar, yBar, barGenisligi, barYukseklik);

                String oranYazi = String.format("%.1f", oran);
                g2.setColor(Color.BLACK);
                int yaziGenislik = fm.stringWidth(oranYazi);
                g2.drawString(oranYazi, xBar + (barGenisligi - yaziGenislik) / 2, yBar - 3);

                // Kategori etiketi 45 derece dondurulmus
                String etiket = kategoriler.get(i);
                if (etiket.length() > 10) etiket = etiket.substring(0, 10) + ".";
                Graphics2D g2e = (Graphics2D) g2.create();
                g2e.rotate(-Math.PI / 4, xBar + barGenisligi / 2.0, kenarBosluk + yukseklik + 12);
                g2e.drawString(etiket,
                        xBar + barGenisligi / 2 - fm.stringWidth(etiket) / 2,
                        kenarBosluk + yukseklik + 15);
                g2e.dispose();
            }

            g2.dispose();
        }

        // Yesil %80+, sari %60-80, turuncu %40-60, kirmizi %40 alti
        private Color oranaGoreRenk(double oran) {
            if (oran >= 80) return new Color(76, 175, 80);
            if (oran >= 60) return new Color(255, 193, 7);
            if (oran >= 40) return new Color(255, 152, 0);
            return new Color(244, 67, 54);
        }
    }

    // ================= Heatmap =================
    private static class HeatmapPaneli extends JPanel {

        private List<String> kategoriler = new ArrayList<>();
        private int[][] matris;
        private int maksimumDeger = 1;

        HeatmapPaneli() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(800, 520));
        }

        void veriyiGuncelle(Map<String, Map<String, Integer>> confusionMatrix) {
            kategoriler.clear();

            if (confusionMatrix == null) {
                matris = null;
                repaint();
                return;
            }

            TreeSet<String> hepsi = new TreeSet<>();
            for (String g : confusionMatrix.keySet()) {
                hepsi.add(g);
                hepsi.addAll(confusionMatrix.get(g).keySet());
            }
            kategoriler.addAll(hepsi);

            int n = kategoriler.size();
            matris = new int[n][n];
            maksimumDeger = 1;

            for (int i = 0; i < n; i++) {
                Map<String, Integer> satir = confusionMatrix.getOrDefault(kategoriler.get(i), new HashMap<>());
                for (int j = 0; j < n; j++) {
                    int deger = satir.getOrDefault(kategoriler.get(j), 0);
                    matris[i][j] = deger;
                    if (deger > maksimumDeger) maksimumDeger = deger;
                }
            }

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (matris == null || kategoriler.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int n = kategoriler.size();
            int solBosluk = 120;
            int ustBosluk = 100;
            int boyut = Math.min((getWidth() - solBosluk - 20) / n,
                    (getHeight() - ustBosluk - 20) / n);
            if (boyut < 10) boyut = 10;

            g2.setFont(g2.getFont().deriveFont(11f));
            FontMetrics fm = g2.getFontMetrics();

            // Hucreler: deger buyudukce beyazdan koyu maviye
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int x = solBosluk + j * boyut;
                    int y = ustBosluk + i * boyut;

                    double yogunluk = (double) matris[i][j] / maksimumDeger;
                    int mavi = 255 - (int) (yogunluk * 200);
                    g2.setColor(new Color(mavi, mavi, 255));
                    g2.fillRect(x, y, boyut, boyut);

                    g2.setColor(Color.GRAY);
                    g2.drawRect(x, y, boyut, boyut);

                    if (matris[i][j] > 0) {
                        String sayiYazi = String.valueOf(matris[i][j]);
                        int yaziG = fm.stringWidth(sayiYazi);
                        g2.setColor(yogunluk > 0.5 ? Color.WHITE : Color.BLACK);
                        g2.drawString(sayiYazi,
                                x + (boyut - yaziG) / 2,
                                y + (boyut + fm.getAscent()) / 2 - 2);
                    }
                }
            }

            // Sol etiketler (gercek kategori)
            g2.setColor(Color.BLACK);
            for (int i = 0; i < n; i++) {
                String etiket = kategoriler.get(i);
                if (etiket.length() > 12) etiket = etiket.substring(0, 11) + ".";
                int y = ustBosluk + i * boyut + (boyut + fm.getAscent()) / 2 - 2;
                g2.drawString(etiket, 5, y);
            }

            // Ust etiketler (tahmin) - dondurulmus
            for (int j = 0; j < n; j++) {
                String etiket = kategoriler.get(j);
                if (etiket.length() > 12) etiket = etiket.substring(0, 11) + ".";
                Graphics2D g2e = (Graphics2D) g2.create();
                int x = solBosluk + j * boyut + boyut / 2;
                g2e.rotate(-Math.PI / 4, x, ustBosluk - 5);
                g2e.drawString(etiket, x - fm.stringWidth(etiket) / 2, ustBosluk - 5);
                g2e.dispose();
            }

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
            g2.drawString("Gercek", 10, ustBosluk - 15);
            g2.drawString("Tahmin", solBosluk + n * boyut / 2 - 20, ustBosluk - 80);

            g2.dispose();
        }
    }
}
