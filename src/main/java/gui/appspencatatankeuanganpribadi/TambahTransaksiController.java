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
        tanggalPicker.setValue(LocalDate.now());

        kategoriComboBox.setItems(kategoriPemasukan);

        setupEventHandlers();
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
            System.out.println("Transaksi berhasil disimpan: " + transaksi);

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
}