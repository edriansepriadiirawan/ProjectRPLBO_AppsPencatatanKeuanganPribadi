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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditHapusTransaksiController implements Initializable {
    @FXML
    public Button tambahTransaksiBtn;
    @FXML
    public Button kelolaKategoriBtn;
    @FXML
    public Button lihatTransaksiBtn;
    @FXML
    public Button ringkasanKeuanganBtn;
    @FXML
    public Button filterKategoriBtn;
    @FXML
    public Button logoutButton;
    @FXML
    public Button pengingatTransaksiBtn;
    @FXML private TableView<Transaksi> transaksiTableView;
    @FXML private TableColumn<Transaksi, Integer> idColumn;
    @FXML private TableColumn<Transaksi, LocalDate> tanggalColumn;
    @FXML private TableColumn<Transaksi, String> jenisColumn;
    @FXML private TableColumn<Transaksi, String> kategoriColumn;
    @FXML private TableColumn<Transaksi, Double> jumlahColumn;

    @FXML private TextField idTransaksiField;
    @FXML private TextField jumlahField;
    @FXML private DatePicker tanggalPicker;
    @FXML private ComboBox<String> kategoriComboBox;
    @FXML private TextArea deskripsiArea;

    @FXML private RadioButton pemasukanRadio;
    @FXML private RadioButton pengeluaranRadio;
    @FXML private ToggleGroup jenisTransaksi;

    @FXML private VBox editFormContainer;
    @FXML private Label noTransaksiLabel;
    @FXML private Label statusLabel;

    @FXML private Button simpanButton;
    @FXML private Button hapusButton;
    @FXML private Button batalButton;
    @FXML private Button kembaliButton;

    private ObservableList<Transaksi> transaksiList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transaksiList = FXCollections.observableArrayList(DataBaseHelper.ambilSemuaTransaksi());
        transaksiTableView.setItems(transaksiList);

        idColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        tanggalColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTanggal()));
        jenisColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipe()));
        kategoriColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKategori()));
        jumlahColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getJumlah()));
        kembaliButton.setOnAction(e -> bukaHalaman("Dashboard.fxml"));
        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));
        kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
        filterKategoriBtn.setOnAction(e -> bukaHalaman("FilterKategori.fxml"));
        pengingatTransaksiBtn.setOnAction(event -> bukaHalaman("Pengingat-Transaksi.fxml"));

        kategoriComboBox.setItems(FXCollections.observableArrayList());

        transaksiTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                isiForm(newSel);
            }
        });

        simpanButton.setOnAction(e -> simpanPerubahan());
        hapusButton.setOnAction(e -> hapusTransaksi());
        batalButton.setOnAction(e -> resetForm());

        resetForm(); // Awal kondisi form
    }

    private void isiForm(Transaksi t) {
        editFormContainer.setVisible(true);
        editFormContainer.setManaged(true);
        noTransaksiLabel.setVisible(false);
        noTransaksiLabel.setManaged(false);

        idTransaksiField.setText(String.valueOf(t.getId()));
        tanggalPicker.setValue(t.getTanggal());
        jumlahField.setText(String.valueOf(t.getJumlah()));
        kategoriComboBox.setValue(t.getKategori());
        deskripsiArea.setText(t.getDeskripsi());

        if ("Pemasukan".equalsIgnoreCase(t.getTipe())) {
            pemasukanRadio.setSelected(true);
        } else {
            pengeluaranRadio.setSelected(true);
        }
    }

    private void simpanPerubahan() {
        try {
            int id = Integer.parseInt(idTransaksiField.getText());
            LocalDate tanggal = tanggalPicker.getValue();
            double jumlah = Double.parseDouble(jumlahField.getText());
            String kategori = kategoriComboBox.getValue();
            String deskripsi = deskripsiArea.getText();
            String tipe = pemasukanRadio.isSelected() ? "Pemasukan" : "Pengeluaran";

            // Update = hapus lama + simpan baru
            DataBaseHelper.hapusTransaksiBerdasarkanId(id);
            Transaksi transaksiBaru = new Transaksi(tanggal, deskripsi, kategori, tipe, jumlah, id);
            DataBaseHelper.simpanTransaksi(transaksiBaru);

            statusLabel.setText("Transaksi berhasil diperbarui.");
            refreshTabel();
            resetForm();
        } catch (Exception e) {
            statusLabel.setText("Gagal menyimpan: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hapusTransaksi() {
        try {
            int id = Integer.parseInt(idTransaksiField.getText());
            DataBaseHelper.hapusTransaksiBerdasarkanId(id);

            statusLabel.setText("Transaksi berhasil dihapus.");
            refreshTabel();
            resetForm();
        } catch (Exception e) {
            statusLabel.setText("Gagal menghapus: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshTabel() {
        transaksiList.setAll(DataBaseHelper.ambilSemuaTransaksi());
    }

    private void resetForm() {
        editFormContainer.setVisible(false);
        editFormContainer.setManaged(false);
        noTransaksiLabel.setVisible(true);
        noTransaksiLabel.setManaged(true);

        idTransaksiField.clear();
        jumlahField.clear();
        tanggalPicker.setValue(null);
        kategoriComboBox.setValue(null);
        deskripsiArea.clear();
        statusLabel.setText("");
    }

    private void bukaHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) kembaliButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Gagal membuka halaman: " + fxmlPath);
        }
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kesalahan");
        alert.setHeaderText("Terjadi Kesalahan");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
