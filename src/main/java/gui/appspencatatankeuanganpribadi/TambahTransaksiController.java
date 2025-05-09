package gui.appspencatatankeuanganpribadi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class TambahTransaksiController implements Initializable {

    @FXML
    private Button backButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button ringkasanKeuanganBtn;

    @FXML
    private Button tambahTransaksiBtn;

    @FXML
    private Button kelolaKategoriBtn;

    @FXML
    private Button lihatTransaksiBtn;

    @FXML
    private Button editTransaksiBtn;

    @FXML
    private Button filterKategoriBtn;

    @FXML
    private Button pengingatTransaksiBtn;

    @FXML
    private RadioButton pemasukanRadio;

    @FXML
    private RadioButton pengeluaranRadio;

    @FXML
    private ToggleGroup jenisTransaksi;

    @FXML
    private TextField jumlahField;

    @FXML
    private DatePicker tanggalPicker;

    @FXML
    private ComboBox<String> kategoriComboBox;

    @FXML
    private TextArea deskripsiArea;

    @FXML
    private Button resetButton;

    @FXML
    private Button simpanButton;

    private ObservableList<String> kategoriPemasukan = FXCollections.observableArrayList(
            "Gaji", "Bonus", "Investasi", "Hadiah", "Penjualan", "Lainnya"
    );

    private ObservableList<String> kategoriPengeluaran = FXCollections.observableArrayList(
            "Makanan", "Transportasi", "Belanja", "Hiburan", "Pendidikan",
            "Tagihan", "Kesehatan", "Rumah", "Lainnya"
    );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));

        tanggalPicker.setValue(LocalDate.now());

        kategoriComboBox.setItems(kategoriPemasukan);

        setupEventHandlers();
    }
    private void bukaHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tambahTransaksiBtn.getScene().getWindow(); // gunakan salah satu button
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Gagal membuka halaman: " + fxmlPath);
        }
    }

    private void setupEventHandlers() {
        pemasukanRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                kategoriComboBox.setItems(kategoriPemasukan);
                kategoriComboBox.getSelectionModel().selectFirst();
            }
        });

        pengeluaranRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                kategoriComboBox.setItems(kategoriPengeluaran);
                kategoriComboBox.getSelectionModel().selectFirst();
            }
        });

        // Handle reset button
        resetButton.setOnAction(event -> {
            pemasukanRadio.setSelected(true);
            jumlahField.clear();
            tanggalPicker.setValue(LocalDate.now());
            kategoriComboBox.setItems(kategoriPemasukan);
            kategoriComboBox.getSelectionModel().selectFirst();
            deskripsiArea.clear();
        });

        // Handle back button
        backButton.setOnAction(event -> kembaliKeDashboard());

        // Handle save button
        simpanButton.setOnAction(this::simpanTransaksi);
    }

    private void simpanTransaksi(ActionEvent event) {
        // Validate input
        if (!validateInput()) {
            return;
        }

        try {

            String tipeTransaksi = pemasukanRadio.isSelected() ? "Pemasukan" : "Pengeluaran";
            double jumlah = Double.parseDouble(jumlahField.getText().replace(".", "").replace(",", "."));
            LocalDate tanggal = tanggalPicker.getValue();
            String kategori = kategoriComboBox.getValue();
            String deskripsi = deskripsiArea.getText();

            Transaksi transaksi = new Transaksi(tanggal, deskripsi, kategori, tipeTransaksi, jumlah);
            DataBaseHelper.simpanTransaksi(transaksi);
            System.out.println("Transaksi disimpan ke database: " + transaksi);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText("Transaksi Berhasil Disimpan");
            alert.setContentText("Data transaksi telah berhasil disimpan ke dalam sistem.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                kembaliKeDashboard();
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Format jumlah tidak valid. Masukkan angka saja.");
        } catch (Exception e) {
            showErrorAlert("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (jumlahField.getText().isEmpty()) {
            errorMessage.append("- Jumlah transaksi harus diisi.\n");
        } else {
            try {
                String normalizedInput = jumlahField.getText().replace(".", "").replace(",", ".");
                double amount = Double.parseDouble(normalizedInput);
                if (amount <= 0) {
                    errorMessage.append("- Jumlah transaksi harus lebih dari 0.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- Format jumlah tidak valid. Masukkan angka saja.\n");
            }
        }

        if (tanggalPicker.getValue() == null) {
            errorMessage.append("- Tanggal transaksi harus dipilih.\n");
        }

        if (kategoriComboBox.getValue() == null || kategoriComboBox.getValue().isEmpty()) {
            errorMessage.append("- Kategori harus dipilih.\n");
        }

        if (errorMessage.length() > 0) {
            showErrorAlert("Mohon perbaiki kesalahan berikut:\n" + errorMessage.toString());
            return false;
        }

        return true;
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Terjadi Kesalahan");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void kembaliKeDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Gagal kembali ke dashboard: " + e.getMessage());
        }
    }
    private void setupButtonActions() {
        tambahTransaksiBtn.setOnAction(event -> gantiScene("Tambah-Transaksi.fxml"));
        kelolaKategoriBtn.setOnAction(event -> gantiScene("Kelola-Kategori.fxml"));
        lihatTransaksiBtn.setOnAction(event -> gantiScene("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(event -> gantiScene("Ringkasan-Keuangan.fxml"));
        editTransaksiBtn.setOnAction(event -> gantiScene("EditHapusTransaksi.fxml"));
        logoutButton.setOnAction(event -> gantiScene("Login.fxml"));

        // Placeholder (belum ada fungsinya)
        filterKategoriBtn.setOnAction(event -> showAlert("Fitur Belum Tersedia", null, "Filter berdasarkan kategori belum tersedia."));
        pengingatTransaksiBtn.setOnAction(event -> showAlert("Fitur Belum Tersedia", null, "Pengingat transaksi belum tersedia."));
    }

    private void gantiScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) logoutButton.getScene().getWindow(); // Bisa gunakan tombol apa pun
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Gagal Pindah Halaman", "Error saat membuka " + fxmlPath, e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}