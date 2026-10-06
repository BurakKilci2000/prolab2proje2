package com.kocaeli.prolab.arayuz;

import javax.swing.*;
import java.awt.*;

// Uygulamanin ana penceresi: 4 sekmeli yapi.
// 1) Veri Yukleme  2) Model Egitimi  3) Sonuclar  4) Grafikler
public class AnaEkran extends JFrame {

    private UygulamaDurumu durum;
    private JTabbedPane sekmeler;

    private VeriYuklePaneli veriPaneli;
    private EgitimPaneli egitimPaneli;
    private SonucPaneli sonucPaneli;
    private GrafikPaneli grafikPaneli;

    public AnaEkran() {
        super("Prolab II - KNN vs Karar Agaci");

        this.durum = new UygulamaDurumu();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sekmeler = new JTabbedPane();

        veriPaneli = new VeriYuklePaneli(durum);
        egitimPaneli = new EgitimPaneli(durum);
        sonucPaneli = new SonucPaneli(durum);
        grafikPaneli = new GrafikPaneli(durum);

        sekmeler.addTab("1. Veri Yukleme", veriPaneli);
        sekmeler.addTab("2. Model Egitimi", egitimPaneli);
        sekmeler.addTab("3. Sonuclar", sonucPaneli);
        sekmeler.addTab("4. Grafikler", grafikPaneli);

        // Sekme acildiginda guncel veriyi goster
        sekmeler.addChangeListener(e -> {
            if (sekmeler.getSelectedComponent() == sonucPaneli) {
                sonucPaneli.yenile();
            } else if (sekmeler.getSelectedComponent() == grafikPaneli) {
                grafikPaneli.yenile();
            }
        });

        add(sekmeler, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Swing bilesenleri EDT (Event Dispatch Thread) uzerinde calismali
        SwingUtilities.invokeLater(() -> {
            AnaEkran pencere = new AnaEkran();
            pencere.setVisible(true);
        });
    }
}
