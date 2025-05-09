package gui.appspencatatankeuanganpribadi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class KelolakategoriKontroller implements Initializable {

    @FXML
    private TextField namaKategoriField;

    @FXML
    private ListView<String> kategoriListView;

    @FXML
    private Button tambahButton, hapusButton, kembaliButton,
            tambahTransaksiBtn, editTransaksiBtn, lihatTransaksiBtn, ringkasanKeuanganBtn,
            kelolaKategoriBtn, logoutButton, filterKategoriBtn, pengingatTransaksiBtn;

    private ObservableList<String> kategoriList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));

        // Placeholder fitur belum tersedia
        filterKategoriBtn.setOnAction(e -> showErrorAlert("Fitur filter kategori belum tersedia."));
        pengingatTransaksiBtn.setOnAction(e -> showErrorAlert("Fitur pengingat transaksi belum tersedia."));

        refreshKategoriList();

        tambahButton.setOnAction(e -> {
            String newKategori = namaKategoriField.getText().trim();
            if (!newKategori.isEmpty()) {
                DataBaseHelper.tambahKategori(newKategori);
                namaKategoriField.clear();
                refreshKategoriList();
            } else {
                showSimpleAlert("Nama kategori tidak boleh kosong.");
            }
        });

        hapusButton.setOnAction(e -> {
            String selected = kategoriListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                DataBaseHelper.hapusKategori(selected);
                refreshKategoriList();
            } else {
                showSimpleAlert("Pilih kategori yang ingin dihapus.");
            }
        });

        kembaliButton.setOnAction(e -> kembaliKeDashboard());
    }

    private void bukaHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tambahTransaksiBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Gagal membuka halaman: " + fxmlPath);
        }
    }

    private void refreshKategoriList() {
        List<String> data = DataBaseHelper.getSemuaKategori();
        kategoriList = FXCollections.observableArrayList(data);
        kategoriListView.setItems(kategoriList);
    }

    private void kembaliKeDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) kembaliButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Gagal kembali ke dashboard.");
        }
    }

    private void showSimpleAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void showErrorAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kesalahan");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}