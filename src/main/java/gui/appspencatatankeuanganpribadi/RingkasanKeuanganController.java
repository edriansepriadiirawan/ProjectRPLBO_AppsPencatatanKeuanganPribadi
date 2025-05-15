package gui.appspencatatankeuanganpribadi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RingkasanKeuanganController implements Initializable {

    @FXML
    private Label totalPemasukanLabel;

    @FXML
    private Label totalPengeluaranLabel;

    @FXML
    private Label saldoLabel;

    @FXML
    private Label tanggalLabel;

    @FXML
    private Button backButton, tambahTransaksiBtn, editTransaksiBtn, lihatTransaksiBtn,
            kelolaKategoriBtn, logoutButton, filterKategoriBtn, pengingatTransaksiBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            double pemasukan = DataBaseHelper.getTotalByTipe("Pemasukan");
            double pengeluaran = DataBaseHelper.getTotalByTipe("Pengeluaran");
            double saldo = pemasukan - pengeluaran;

            totalPemasukanLabel.setText(String.format("Rp %,d", (long) pemasukan));
            totalPengeluaranLabel.setText(String.format("Rp %,d", (long) pengeluaran));
            saldoLabel.setText(String.format("Rp %,d", (long) saldo));

            if (tanggalLabel != null) {
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                tanggalLabel.setText("Tanggal: " + today.format(formatter));
            }

            backButton.setOnAction(e -> bukaHalaman("Dashboard.fxml"));
            tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
            editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
            lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
            kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
            logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));

            filterKategoriBtn.setOnAction(e -> showAlert("Informasi", "Fitur filter kategori belum tersedia."));
            pengingatTransaksiBtn.setOnAction(e -> showAlert("Informasi", "Fitur pengingat transaksi belum tersedia."));

        } catch (Exception e) {
            showAlert("Error", "Gagal memuat data ringkasan keuangan.");
            e.printStackTrace();
        }
    }

    private void bukaHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Gagal membuka halaman: " + fxmlPath);
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kesalahan");
        alert.setHeaderText("Terjadi Kesalahan");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
