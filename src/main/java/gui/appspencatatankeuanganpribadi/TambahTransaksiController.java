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
import java.util.stream.Collectors;

public class TambahTransaksiController implements Initializable {
    @FXML
    public Button logoutButton;
    @FXML
    public Button filterKategoriBtn;
    @FXML
    private Button backButton, resetButton, simpanButton;
    @FXML
    private RadioButton pemasukanRadio, pengeluaranRadio;
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
    private Button editTransaksiBtn, lihatTransaksiBtn, ringkasanKeuanganBtn, kelolaKategoriBtn;

    private ObservableList<Kategori> semuaKategoriObjek;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tanggalPicker.setValue(LocalDate.now());

        // Muat kategori default (Pemasukan)
        loadKategoriKeComboBox("Pemasukan");

        // Event RadioButton
        pemasukanRadio.setOnAction(e -> loadKategoriKeComboBox("Pemasukan"));
        pengeluaranRadio.setOnAction(e -> loadKategoriKeComboBox("Pengeluaran"));

        // Navigasi
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
        filterKategoriBtn.setOnAction(e -> bukaHalaman("FilterKategori.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));

        setupEventHandlers();
    }

    private void loadKategoriKeComboBox(String jenis) {
        semuaKategoriObjek = FXCollections.observableArrayList(DataBaseHelper.getSemuaKategoriObjek());
        ObservableList<String> namaKategoriList = FXCollections.observableArrayList(
                semuaKategoriObjek.stream()
                        .filter(k -> k.getJenis().equalsIgnoreCase(jenis))
                        .map(Kategori::getNama)
                        .collect(Collectors.toList())
        );
        kategoriComboBox.setItems(namaKategoriList);
        if (!namaKategoriList.isEmpty()) {
            kategoriComboBox.getSelectionModel().selectFirst();
        } else {
            kategoriComboBox.getSelectionModel().clearSelection();
        }
    }

    private void setupEventHandlers() {
        resetButton.setOnAction(event -> {
            pemasukanRadio.setSelected(true);
            jumlahField.clear();
            tanggalPicker.setValue(LocalDate.now());
            deskripsiArea.clear();
            loadKategoriKeComboBox("Pemasukan");
        });

        backButton.setOnAction(event -> kembaliKeDashboard());
        simpanButton.setOnAction(this::simpanTransaksi);
    }

    private void simpanTransaksi(ActionEvent event) {
        if (!validateInput()) return;

        try {
            String tipeTransaksi = pemasukanRadio.isSelected() ? "Pemasukan" : "Pengeluaran";
            double jumlah = Double.parseDouble(jumlahField.getText().replace(".", "").replace(",", "."));
            LocalDate tanggal = tanggalPicker.getValue();
            String kategori = kategoriComboBox.getValue();
            String deskripsi = deskripsiArea.getText();

            Transaksi transaksi = new Transaksi(tanggal, deskripsi, kategori, tipeTransaksi, jumlah);
            DataBaseHelper.simpanTransaksi(transaksi);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText("Transaksi Berhasil Disimpan");
            alert.setContentText("Data transaksi telah berhasil disimpan.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                kembaliKeDashboard();
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Format jumlah tidak valid. Gunakan angka saja.");
        } catch (Exception e) {
            showErrorAlert("Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        String jumlahText = jumlahField.getText();
        if (jumlahText.isEmpty()) {
            errorMessage.append("- Jumlah transaksi harus diisi.\n");
        } else {
            try {
                double amount = Double.parseDouble(jumlahText.replace(".", "").replace(",", "."));
                if (amount <= 0) {
                    errorMessage.append("- Jumlah harus lebih dari 0.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- Format jumlah tidak valid.\n");
            }
        }

        if (tanggalPicker.getValue() == null) {
            errorMessage.append("- Tanggal transaksi harus dipilih.\n");
        }

        if (kategoriComboBox.getValue() == null || kategoriComboBox.getValue().isEmpty()) {
            errorMessage.append("- Kategori harus dipilih.\n");
        }

        if (errorMessage.length() > 0) {
            showErrorAlert("Mohon perbaiki kesalahan berikut:\n" + errorMessage);
            return false;
        }

        return true;
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kesalahan");
        alert.setHeaderText("Terjadi Kesalahan");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void kembaliKeDashboard() {
        bukaHalaman("Dashboard.fxml");
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
}
