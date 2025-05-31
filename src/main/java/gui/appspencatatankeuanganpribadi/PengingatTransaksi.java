package gui.appspencatatankeuanganpribadi;

import java.time.LocalDate;

public class PengingatTransaksi {
    private int id;
    private String namaTransaksi;
    private double jumlah;
    private String kategori;
    private LocalDate tanggalPengingat;
    private String waktu;
    private String pengulangan;
    private String status;
    private String catatan;

    public PengingatTransaksi(int id, String namaTransaksi, double jumlah, String kategori,
                              LocalDate tanggalPengingat, String waktu, String pengulangan,
                              String status, String catatan) {
        this.id = id;
        this.namaTransaksi = namaTransaksi;
        this.jumlah = jumlah;
        this.kategori = kategori;
        this.tanggalPengingat = tanggalPengingat;
        this.waktu = waktu;
        this.pengulangan = pengulangan;
        this.status = status;
        this.catatan = catatan;
    }

    // Getters dan Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaTransaksi() { return namaTransaksi; }
    public void setNamaTransaksi(String namaTransaksi) { this.namaTransaksi = namaTransaksi; }

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public LocalDate getTanggalPengingat() { return tanggalPengingat; }
    public void setTanggalPengingat(LocalDate tanggalPengingat) { this.tanggalPengingat = tanggalPengingat; }

    public String getWaktu() { return waktu; }
    public void setWaktu(String waktu) { this.waktu = waktu; }

    public String getPengulangan() { return pengulangan; }
    public void setPengulangan(String pengulangan) { this.pengulangan = pengulangan; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}
