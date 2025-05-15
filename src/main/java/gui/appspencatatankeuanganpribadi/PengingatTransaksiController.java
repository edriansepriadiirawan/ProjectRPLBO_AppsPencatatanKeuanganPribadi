package gui.appspencatatankeuanganpribadi;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;

public class PengingatTransaksiController {

    @FXML
    private AnchorPane pengingatTransaksiPane;

    @FXML
    private Button btnKembali;

    @FXML
    private Button btnSimpan;

    @FXML
    private ComboBox<String> comboKategori;

    @FXML
    private ComboBox<String> comboJenis;

    @FXML
    private DatePicker datePickerTanggal;

    @FXML
    private TextField textJumlah;

    @FXML
    private TextArea textDeskripsi;

    @FXML
    private Label labelStatus;

    @FXML
    void initialize() {
        // Inisialisasi pilihan combo box
        comboJenis.getItems().addAll("Pemasukan", "Pengeluaran");
        comboKategori.getItems().addAll("Makan", "Transportasi", "Belanja", "Lainnya"); // bisa diubah sesuai kebutuhan
    }

    @FXML
    void handleSimpanAction(ActionEvent event) {
        String jenis = comboJenis.getValue();
        String kategori = comboKategori.getValue();
        String tanggal = (datePickerTanggal.getValue() != null) ? datePickerTanggal.getValue().toString() : "";
        String jumlah = textJumlah.getText();
        String deskripsi = textDeskripsi.getText();

        if (jenis == null || kategori == null || tanggal.isEmpty() || jumlah.isEmpty()) {
            labelStatus.setText("Harap isi semua data yang dibutuhkan.");
        } else {
            // Simulasi penyimpanan pengingat (bisa diganti dengan DB atau file)
            System.out.println("Pengingat disimpan:");
            System.out.println("Jenis: " + jenis);
            System.out.println("Kategori: " + kategori);
            System.out.println("Tanggal: " + tanggal);
            System.out.println("Jumlah: " + jumlah);
            System.out.println("Deskripsi: " + deskripsi);

            labelStatus.setText("Pengingat berhasil disimpan.");
        }
    }

    @FXML
    void handleKembaliAction(ActionEvent event) {
        // Tambahkan logika untuk kembali ke dashboard atau halaman sebelumnya
        System.out.println("Kembali ke dashboard...");
    }
}
