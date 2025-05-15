package gui.appspencatatankeuanganpribadi;

public class Kategori {
    private int id;
    private String nama;
    private String jenis;
    private String ikon;

    public Kategori(int id, String nama, String jenis, String ikon) {
        this.id = id;
        this.nama = nama;
        this.jenis = jenis;
        this.ikon = ikon;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getJenis() { return jenis; }
    public String getIkon() { return ikon; }
}
