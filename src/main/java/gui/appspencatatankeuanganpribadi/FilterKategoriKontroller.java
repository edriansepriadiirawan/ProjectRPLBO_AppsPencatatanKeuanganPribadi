package gui.appspencatatankeuanganpribadi;

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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FilterKategoriKontroller implements Initializable {
    @FXML
    public Button tambahTransaksiBtn;
    @FXML
    public Button kelolaKategoriBtn;
    @FXML
    public Button ringkasanKeuanganBtn;
    @FXML
    public Button editTransaksiBtn;
    @FXML
    public Button filterKategoriBtn;
    @FXML
    public Button pengingatTransaksiBtn;
    @FXML
    public Button logoutButton;
    @FXML
    public Button lihatTransaksiBtn;
    @FXML
    public Button PengingatTransaksiBtn;

    @FXML private ComboBox<String> kategoriComboBox;
    @FXML private Button filterButton, resetButton, kembaliButton;
    @FXML private TableView<Transaksi> transaksiTableView;
    @FXML private TableColumn<Transaksi, String> tanggalColumn, deskripsiColumn, kategoriColumn, tipeColumn;
    @FXML private TableColumn<Transaksi, Double> jumlahColumn;
    @FXML private Label totalTransaksiLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set kolom tabel

        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalString"));
        deskripsiColumn.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        kategoriColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        tipeColumn.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        jumlahColumn.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        pengingatTransaksiBtn.setOnAction(event -> bukaHalaman("Pengingat-Transaksi.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));
        transaksiTableView.setItems(FXCollections.observableArrayList(DataBaseHelper.ambilSemuaTransaksi()));



        // Ambil semua kategori dari database
        List<Kategori> kategoriDB = DataBaseHelper.getSemuaKategoriObjek();
        List<String> namaKategori = kategoriDB.stream()
                .map(Kategori::getNama)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        kategoriComboBox.setItems(FXCollections.observableArrayList(namaKategori));

        // Event handler
        filterButton.setOnAction(e -> filterTransaksi());
        resetButton.setOnAction(e -> resetFilter());
        kembaliButton.setOnAction(e -> bukaHalaman("Dashboard.fxml"));
    }

    private void filterTransaksi() {
        String selectedKategori = kategoriComboBox.getValue();
        if (selectedKategori == null || selectedKategori.isEmpty()) {
            showAlert("Silakan pilih kategori terlebih dahulu.");
            return;
        }

        List<Transaksi> semua = DataBaseHelper.ambilSemuaTransaksi();
        System.out.println("[DEBUG] Jumlah semua transaksi: " + semua.size());

        List<Transaksi> hasil = semua.stream()
                .filter(t -> t.getKategori() != null &&
                        t.getKategori().trim().equalsIgnoreCase(selectedKategori.trim()))
                .collect(Collectors.toList());

        System.out.println("[DEBUG] Ditemukan transaksi cocok: " + hasil.size());

        transaksiTableView.setItems(FXCollections.observableArrayList(hasil));
        totalTransaksiLabel.setText("Total Transaksi: " + hasil.size());
        semua.forEach(t -> System.out.println("[DATA] " + t.getTanggal() + " | " + t.getKategori()));

    }


    private void resetFilter() {
        kategoriComboBox.getSelectionModel().clearSelection();

        // Isi ulang tabel dengan semua transaksi
        List<Transaksi> semuaTransaksi = DataBaseHelper.ambilSemuaTransaksi();
        transaksiTableView.setItems(FXCollections.observableArrayList(semuaTransaksi));

        totalTransaksiLabel.setText("Total Transaksi: " + semuaTransaksi.size());
    }


    private void bukaHalaman(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) kembaliButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Gagal membuka halaman: " + fxmlPath);
        }
    }

    private void showAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
