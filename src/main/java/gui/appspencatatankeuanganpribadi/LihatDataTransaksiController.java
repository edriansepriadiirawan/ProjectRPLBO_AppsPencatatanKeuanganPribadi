package gui.appspencatatankeuanganpribadi;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LihatDataTransaksiController implements Initializable {
    @FXML
    public Label totalTransaksiLabel;
    @FXML
    public Label totalPemasukanLabel;
    @FXML
    public Label totalPengeluaranLabel;
    @FXML
    public Label saldoLabel;
    @FXML
    public Button filterKategoriBtn;
    @FXML private TableView<Transaksi> transaksiTable;
    @FXML private TableColumn<Transaksi, Integer> idColumn;
    @FXML private TableColumn<Transaksi, LocalDate> tanggalColumn;
    @FXML private TableColumn<Transaksi, String> deskripsiColumn;
    @FXML private TableColumn<Transaksi, String> kategoriColumn;
    @FXML private TableColumn<Transaksi, String> tipeColumn;
    @FXML private TableColumn<Transaksi, Double> jumlahColumn;

    @FXML private DatePicker filterTanggal;
    @FXML private Button resetFilterButton;
    @FXML private Button kembaliButton;
    @FXML private Button tambahTransaksiBtn;
    @FXML private Button kelolaKategoriBtn;
    @FXML private Button ringkasanKeuanganBtn;
    @FXML private Button editTransaksiBtn;
    @FXML private Button logoutButton;


    private final ObservableList<Transaksi> transaksiList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Konfigurasi kolom tabel
        tanggalColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTanggal()));
        deskripsiColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDeskripsi()));
        kategoriColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKategori()));
        tipeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipe()));
        jumlahColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getJumlah()));


        transaksiTable.setItems(transaksiList);

        // Ambil dan tampilkan semua data saat awal
        loadSemuaTransaksi();
        updateRingkasan(transaksiList);  // ✅ Diganti dari semuaTransaksi ke transaksiList

        // Event listener
        filterTanggal.setOnAction(e -> terapkanFilter());
        resetFilterButton.setOnAction(e -> resetFilter());
        kembaliButton.setOnAction(e -> bukaHalaman("Dashboard.fxml"));
        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
        filterKategoriBtn.setOnAction(e -> bukaHalaman("FilterKategori.fxml"));

        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));

    }

    private void loadSemuaTransaksi() {
        List<Transaksi> semua = DataBaseHelper.ambilSemuaTransaksi();
        transaksiList.setAll(semua);
    }

    private void terapkanFilter() {
        LocalDate tanggalDipilih = filterTanggal.getValue();

        if (tanggalDipilih == null) {
            loadSemuaTransaksi();
            updateRingkasan(transaksiList);  // Gunakan data saat ini
            return;
        }

        List<Transaksi> hasil = DataBaseHelper.ambilSemuaTransaksi().stream()
                .filter(t -> t.getTanggal().equals(tanggalDipilih))
                .collect(Collectors.toList());

        transaksiList.setAll(hasil);
        updateRingkasan(hasil);
    }

    private void resetFilter() {
        filterTanggal.setValue(null);
        loadSemuaTransaksi();
        updateRingkasan(transaksiList);  // transaksiList sekarang sudah berisi semua data
    }


    private void updateRingkasan(List<Transaksi> data) {
        int totalTransaksi = data.size();
        double totalPemasukan = data.stream()
                .filter(t -> t.getTipe().equalsIgnoreCase("Pemasukan"))
                .mapToDouble(Transaksi::getJumlah)
                .sum();
        double totalPengeluaran = data.stream()
                .filter(t -> t.getTipe().equalsIgnoreCase("Pengeluaran"))
                .mapToDouble(Transaksi::getJumlah)
                .sum();
        double saldo = totalPemasukan - totalPengeluaran;

        totalTransaksiLabel.setText("Total Transaksi: " + totalTransaksi);
        totalPemasukanLabel.setText(String.format("Total Pemasukan: Rp %.2f", totalPemasukan));
        totalPengeluaranLabel.setText(String.format("Total Pengeluaran: Rp %.2f", totalPengeluaran));
        saldoLabel.setText(String.format("Saldo: Rp %.2f", saldo));
    }

    private void bukaHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) transaksiTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showErrorAlert("Gagal membuka halaman: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kesalahan");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
