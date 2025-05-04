package gui.appspencatatankeuanganpribadi;

import java.time.LocalDate;

public class Transaksi {
    private LocalDate tanggal;
    private String deskripsi;
    private String kategori;
    private String tipe;  // "Pemasukan" atau "Pengeluaran"
    private double jumlah;

    public Transaksi(LocalDate tanggal, String deskripsi, String kategori, String tipe, double jumlah) {
        this.tanggal = tanggal;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.tipe = tipe;
        this.jumlah = jumlah;
    }

    // Getters
    public LocalDate getTanggal() {
        return tanggal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getKategori() {
        return kategori;
    }

    public String getTipe() {
        return tipe;
    }

    public double getJumlah() {
        return jumlah;
    }

    // Setters
    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }

    @Override
    public String toString() {
        return "Transaksi{" +
                "tanggal=" + tanggal +
                ", deskripsi='" + deskripsi + '\'' +
                ", kategori='" + kategori + '\'' +
                ", tipe='" + tipe + '\'' +
                ", jumlah=" + jumlah +
                '}';
    }
}