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

public class KelolakategoriKontroller implements Initializable {
    @FXML
    public Button filterKategoriBtn;
    @FXML private Button logoutButton, tambahTransaksiBtn, editTransaksiBtn, lihatTransaksiBtn, ringkasanKeuanganBtn;
    @FXML private Button tambahButton, hapusButton, editButton, resetButton, kembaliButton, pengingatTransaksiBtn;

    @FXML private TextField namaKategoriField;
    @FXML private ComboBox<String> jenisKategoriComboBox;

    @FXML private TableView<Kategori> kategoriTableView;
    @FXML private TableColumn<Kategori, Integer> idColumn;
    @FXML private TableColumn<Kategori, String> namaKategoriColumn;
    @FXML private TableColumn<Kategori, String> jenisKategoriColumn;


    @FXML private Label statusLabel;
    private ObservableList<Kategori> kategoriList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inisialisasi ComboBox
        jenisKategoriComboBox.setItems(FXCollections.observableArrayList("Pemasukan", "Pengeluaran"));

        // Inisialisasi kolom tabel
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaKategoriColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        jenisKategoriColumn.setCellValueFactory(new PropertyValueFactory<>("jenis"));


        // Load data awal
        refreshKategoriList();

        // Event tombol
        tambahButton.setOnAction(e -> tambahKategori());
        hapusButton.setOnAction(e -> hapusKategori());
        editButton.setOnAction(e -> editKategori());
        resetButton.setOnAction(e -> clearForm());

        kembaliButton.setOnAction(e -> bukaHalaman("Dashboard.fxml"));
        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        filterKategoriBtn.setOnAction(e -> bukaHalaman("FilterKategori.fxml"));
        pengingatTransaksiBtn.setOnAction(event -> bukaHalaman("Pengingat-Transaksi.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));

        // Ketika klik baris di tabel
        kategoriTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                namaKategoriField.setText(newVal.getNama());
                jenisKategoriComboBox.setValue(newVal.getJenis());
            }
        });
    }

    private void refreshKategoriList() {
        List<Kategori> data = DataBaseHelper.getSemuaKategoriObjek();
        kategoriList = FXCollections.observableArrayList(data);
        kategoriTableView.setItems(kategoriList);
        statusLabel.setText("Total Kategori: " + data.size());
    }

    private void tambahKategori() {
        String nama = namaKategoriField.getText().trim();
        String jenis = jenisKategoriComboBox.getValue();
        String ikon = "";

        if (nama.isEmpty() || jenis == null) {
            showAlert("Semua field harus diisi.");
            return;
        }

        DataBaseHelper.tambahKategoriBaru(nama, jenis, ikon);
        refreshKategoriList();
        clearForm();
    }

    private void hapusKategori() {
        Kategori selected = kategoriTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DataBaseHelper.hapusKategoriById(selected.getId());
            refreshKategoriList();
            clearForm();
        } else {
            showAlert("Pilih kategori yang ingin dihapus.");
        }
    }

    private void editKategori() {
        Kategori selected = kategoriTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String nama = namaKategoriField.getText().trim();
            String jenis = jenisKategoriComboBox.getValue();
            String ikon = "";

            if (nama.isEmpty() || jenis == null) {
                showAlert("Semua field harus diisi.");
                return;
            }

            DataBaseHelper.updateKategori(selected.getId(), nama, jenis, ikon);
            refreshKategoriList();
            clearForm();
        } else {
            showAlert("Pilih kategori yang ingin diedit.");
        }
    }

    private void clearForm() {
        namaKategoriField.clear();
        jenisKategoriComboBox.getSelectionModel().clearSelection();

    }

    private void bukaHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) kembaliButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Gagal membuka halaman: " + fxmlPath);
            e.printStackTrace();
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
