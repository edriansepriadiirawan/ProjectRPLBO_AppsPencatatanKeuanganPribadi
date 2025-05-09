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

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditHapusTransaksiController implements Initializable {

    @FXML
    private TableView<Transaksi> transaksiTableView;

    @FXML
    private TableColumn<Transaksi, LocalDate> tanggalColumn;

    @FXML
    private TableColumn<Transaksi, String> deskripsiColumn;

    @FXML
    private TableColumn<Transaksi, String> kategoriColumn;

    @FXML
    private TableColumn<Transaksi, String> tipeColumn;

    @FXML
    private TableColumn<Transaksi, Double> jumlahColumn;

    @FXML
    private Button hapusButton, backButton, tambahTransaksiBtn, editTransaksiBtn, lihatTransaksiBtn,
            ringkasanKeuanganBtn, kelolaKategoriBtn, logoutButton, filterKategoriBtn, pengingatTransaksiBtn, dashboardBtn;

    private ObservableList<Transaksi> transaksiList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transaksiList = FXCollections.observableArrayList(DataBaseHelper.ambilSemuaTransaksi());

        tanggalColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getTanggal()));
        deskripsiColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDeskripsi()));
        kategoriColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getKategori()));
        tipeColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipe()));
        jumlahColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getJumlah()));

        transaksiTableView.setItems(transaksiList);

        hapusButton.setOnAction(e -> hapusTransaksi());
        backButton.setOnAction(e -> bukaHalaman("Dashboard.fxml"));

        // Navigasi tambahan
        dashboardBtn.setOnAction(e -> bukaHalaman("Dashboard.fxml"));
        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));

        filterKategoriBtn.setOnAction(e -> showInfoAlert("Fitur belum tersedia."));
        pengingatTransaksiBtn.setOnAction(e -> showInfoAlert("Fitur belum tersedia."));
    }

    private void hapusTransaksi() {
        Transaksi selected = transaksiTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorAlert("Silakan pilih transaksi yang ingin dihapus.");
            return;
        }

        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Hapus");
        konfirmasi.setHeaderText("Yakin ingin menghapus transaksi ini?");
        konfirmasi.setContentText(selected.toString());

        Optional<ButtonType> result = konfirmasi.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DataBaseHelper.hapusTransaksi(selected);
            transaksiList.remove(selected);
            showInfoAlert("Transaksi berhasil dihapus.");
        }
    }

    private void bukaHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Gagal membuka halaman: " + fxmlPath);
        }
    }

    private void showErrorAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Terjadi Kesalahan");
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void showInfoAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}