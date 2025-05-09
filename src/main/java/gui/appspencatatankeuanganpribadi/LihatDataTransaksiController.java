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
import gui.appspencatatankeuanganpribadi.DataBaseHelper;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LihatDataTransaksiController implements Initializable {

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
    private Button kembaliButton, tambahTransaksiBtn, editTransaksiBtn, lihatTransaksiBtn,
            ringkasanKeuanganBtn, kelolaKategoriBtn, logoutButton, filterKategoriBtn, pengingatTransaksiBtn;

    @FXML
    private ComboBox<String> tipeFilterComboBox;

    @FXML
    private ComboBox<String> kategoriFilterComboBox;

    @FXML
    private ObservableList<Transaksi> transaksiList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transaksiList = FXCollections.observableArrayList(DataBaseHelper.ambilSemuaTransaksi());

        tanggalColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getTanggal()));
        deskripsiColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDeskripsi()));
        kategoriColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getKategori()));
        tipeColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTipe()));
        jumlahColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getJumlah()));

        tipeFilterComboBox.setItems(FXCollections.observableArrayList("Semua", "Pemasukan", "Pengeluaran"));
        tipeFilterComboBox.getSelectionModel().selectFirst();

        List<String> kategori = new ArrayList<>();
        kategori.add("Semua");
        kategori.addAll(DataBaseHelper.getSemuaKategori());
        kategoriFilterComboBox.setItems(FXCollections.observableArrayList(kategori));
        kategoriFilterComboBox.getSelectionModel().selectFirst();
        transaksiTableView.setItems(transaksiList);

        kembaliButton.setOnAction(e -> bukaHalaman("Dashboard.fxml"));

        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));

        filterKategoriBtn.setOnAction(e -> showAlert("Fitur belum tersedia."));
        pengingatTransaksiBtn.setOnAction(e -> showAlert("Fitur belum tersedia."));
    }

    private void bukaHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) kembaliButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Gagal membuka halaman: " + fxmlPath);
        }
    }

    @FXML
    private void terapkanFilter() {
        String tipeDipilih = tipeFilterComboBox.getValue();
        String kategoriDipilih = kategoriFilterComboBox.getValue();

        List<Transaksi> semua = DataBaseHelper.ambilSemuaTransaksi();
        List<Transaksi> hasil = semua.stream()
                .filter(t -> {
                    boolean cocokTipe = tipeDipilih.equals("Semua") || t.getTipe().equalsIgnoreCase(tipeDipilih);
                    boolean cocokKategori = kategoriDipilih.equals("Semua") || t.getKategori().equalsIgnoreCase(kategoriDipilih);
                    return cocokTipe && cocokKategori;
                })
                .collect(Collectors.toList());

        transaksiList.setAll(hasil);
    }

    private void showAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
