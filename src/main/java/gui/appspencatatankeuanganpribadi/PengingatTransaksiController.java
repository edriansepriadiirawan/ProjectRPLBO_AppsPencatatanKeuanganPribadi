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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class PengingatTransaksiController implements Initializable {

    // Navigasi Buttons
    @FXML private Button logoutButton;
    @FXML private Button tambahTransaksiBtn;
    @FXML private Button lihatTransaksiBtn;
    @FXML private Button ringkasanKeuanganBtn;
    @FXML private Button editTransaksiBtn;
    @FXML private Button filterKategoriBtn;
    @FXML private Button kembaliButton;

    // Form Controls
    @FXML private ComboBox<String> transaksiComboBox;
    @FXML private Label namaTransaksiLabel;
    @FXML private Label jumlahLabel;
    @FXML private Label kategoriLabel;
    @FXML private Label tanggalTransaksiLabel;
    @FXML private DatePicker tanggalPengingatPicker;
    @FXML private ComboBox<String> jamComboBox;
    @FXML private ComboBox<String> menitComboBox;
    @FXML private ComboBox<String> pengulanganComboBox;
    @FXML private TextArea catatanTextArea;

    // Action Buttons
    @FXML private Button resetButton;
    @FXML private Button tambahButton;
    @FXML private Button editButton;
    @FXML private Button hapusButton;
    @FXML private Button refreshButton;

    // Table dan Display
    @FXML private TableView<PengingatTransaksi> pengingatTableView;
    @FXML private TableColumn<PengingatTransaksi, Integer> idColumn;
    @FXML private TableColumn<PengingatTransaksi, String> namaTransaksiColumn;
    @FXML private TableColumn<PengingatTransaksi, Double> jumlahColumn;
    @FXML private TableColumn<PengingatTransaksi, String> kategoriColumn;
    @FXML private TableColumn<PengingatTransaksi, LocalDate> tanggalPengingatColumn;
    @FXML private TableColumn<PengingatTransaksi, String> waktuColumn;
    @FXML private TableColumn<PengingatTransaksi, String> pengulanganColumn;
    @FXML private TableColumn<PengingatTransaksi, String> statusColumn;

    @FXML private CheckBox tampilkanSemuaCheckBox;
    @FXML private Label statusLabel;
    @FXML private Label totalPengingatLabel;
    @FXML private Label pengingatAktifLabel;
    @FXML private Label pengingatSelesaiLabel;

    // Notifikasi Panel
    @FXML private Label notifikasiPanel;
    @FXML private Button tandaiSelesaiButton;
    @FXML private Button tundaButton;
    @FXML private Button tutupNotifikasiButton;

    private ObservableList<PengingatTransaksi> pengingatList;
    private List<Transaksi> semuaTransaksi;
    private PengingatTransaksi pengingatTerpilih;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeNavigationButtons();
        initializeFormControls();
        initializeTable();
        initializeEventHandlers();
        loadData();
        updateStatistik();
    }

    private void initializeNavigationButtons() {
        kembaliButton.setOnAction(e -> bukaHalaman("Dashboard.fxml"));
        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        filterKategoriBtn.setOnAction(e -> bukaHalaman("FilterKategori.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));
    }

    private void initializeFormControls() {
        // Inisialisasi ComboBox jam (00-23)
        ObservableList<String> jamList = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            jamList.add(String.format("%02d", i));
        }
        jamComboBox.setItems(jamList);

        // Inisialisasi ComboBox menit (00, 15, 30, 45)
        menitComboBox.setItems(FXCollections.observableArrayList("00", "15", "30", "45"));

        // Inisialisasi ComboBox pengulangan
        pengulanganComboBox.setItems(FXCollections.observableArrayList(
                "Sekali", "Harian", "Mingguan", "Bulanan", "Tahunan"
        ));

        // Set default values
        jamComboBox.setValue("08");
        menitComboBox.setValue("00");
        pengulanganComboBox.setValue("Sekali");
    }

    private void initializeTable() {
        idColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        namaTransaksiColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaTransaksi()));
        jumlahColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getJumlah()));
        kategoriColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKategori()));
        tanggalPengingatColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getTanggalPengingat()));
        waktuColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getWaktu()));
        pengulanganColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPengulangan()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        pengingatList = FXCollections.observableArrayList();
        pengingatTableView.setItems(pengingatList);
    }

    private void initializeEventHandlers() {
        // Form action buttons
        tambahButton.setOnAction(e -> tambahPengingat());
        editButton.setOnAction(e -> editPengingat());
        hapusButton.setOnAction(e -> hapusPengingat());
        resetButton.setOnAction(e -> resetForm());
        refreshButton.setOnAction(e -> refreshData());

        // Notifikasi buttons
        tandaiSelesaiButton.setOnAction(e -> tandaiSelesai());
        tundaButton.setOnAction(e -> tundaPengingat());
        tutupNotifikasiButton.setOnAction(e -> tutupNotifikasi());

        // Event listeners
        transaksiComboBox.setOnAction(e -> updateDetailTransaksi());
        tampilkanSemuaCheckBox.setOnAction(e -> filterPengingat());

        pengingatTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                pilihPengingat(newVal);
            }
        });
    }

    private void loadData() {
        // Load semua transaksi untuk ComboBox
        semuaTransaksi = DataBaseHelper.ambilSemuaTransaksi();
        ObservableList<String> transaksiItems = FXCollections.observableArrayList();

        for (Transaksi t : semuaTransaksi) {
            String item = String.format("%s - %s (Rp %.0f)",
                    t.getDeskripsi(), t.getKategori(), t.getJumlah());
            transaksiItems.add(item);
        }
        transaksiComboBox.setItems(transaksiItems);

        // Load pengingat (sementara gunakan data dummy)
        loadPengingatData();
    }

    private void loadPengingatData() {
        // Sementara menggunakan data dummy
        // Nanti bisa diganti dengan DataBaseHelper.ambilSemuaPengingat()
        pengingatList.clear();

        // Contoh data dummy
        pengingatList.add(new PengingatTransaksi(1, "Bayar Listrik", 150000.0, "Utilitas",
                LocalDate.now().plusDays(3), "08:00", "Bulanan", "Aktif", "Jangan lupa bayar listrik"));
        pengingatList.add(new PengingatTransaksi(2, "Gaji Bulanan", 5000000.0, "Gaji",
                LocalDate.now().plusDays(25), "09:00", "Bulanan", "Aktif", "Catat gaji masuk"));
    }

    private void updateDetailTransaksi() {
        int selectedIndex = transaksiComboBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < semuaTransaksi.size()) {
            Transaksi t = semuaTransaksi.get(selectedIndex);
            namaTransaksiLabel.setText("Nama: " + t.getDeskripsi());
            jumlahLabel.setText("Jumlah: Rp " + String.format("%,.0f", t.getJumlah()));
            kategoriLabel.setText("Kategori: " + t.getKategori());
            tanggalTransaksiLabel.setText("Tanggal: " + t.getTanggal().toString());
        } else {
            resetDetailTransaksi();
        }
    }

    private void resetDetailTransaksi() {
        namaTransaksiLabel.setText("Nama: -");
        jumlahLabel.setText("Jumlah: -");
        kategoriLabel.setText("Kategori: -");
        tanggalTransaksiLabel.setText("Tanggal: -");
    }

    private void tambahPengingat() {
        if (!validasiForm()) return;

        int selectedIndex = transaksiComboBox.getSelectionModel().getSelectedIndex();
        Transaksi t = semuaTransaksi.get(selectedIndex);

        LocalDate tanggalPengingat = tanggalPengingatPicker.getValue();
        String waktu = jamComboBox.getValue() + ":" + menitComboBox.getValue();
        String pengulangan = pengulanganComboBox.getValue();
        String catatan = catatanTextArea.getText().trim();

        // Sementara tambah ke list (nanti bisa pakai DataBaseHelper)
        int newId = pengingatList.size() + 1;
        PengingatTransaksi pengingat = new PengingatTransaksi(
                newId, t.getDeskripsi(), t.getJumlah(), t.getKategori(),
                tanggalPengingat, waktu, pengulangan, "Aktif", catatan
        );

        pengingatList.add(pengingat);
        resetForm();
        updateStatistik();
        statusLabel.setText("Pengingat berhasil ditambahkan!");
    }

    private void editPengingat() {
        if (pengingatTerpilih == null) {
            showAlert("Pilih pengingat yang ingin diedit.");
            return;
        }

        if (!validasiForm()) return;

        int selectedIndex = transaksiComboBox.getSelectionModel().getSelectedIndex();
        Transaksi t = semuaTransaksi.get(selectedIndex);

        pengingatTerpilih.setNamaTransaksi(t.getDeskripsi());
        pengingatTerpilih.setJumlah(t.getJumlah());
        pengingatTerpilih.setKategori(t.getKategori());
        pengingatTerpilih.setTanggalPengingat(tanggalPengingatPicker.getValue());
        pengingatTerpilih.setWaktu(jamComboBox.getValue() + ":" + menitComboBox.getValue());
        pengingatTerpilih.setPengulangan(pengulanganComboBox.getValue());
        pengingatTerpilih.setCatatan(catatanTextArea.getText().trim());

        pengingatTableView.refresh();
        resetForm();
        updateStatistik();
        statusLabel.setText("Pengingat berhasil diupdate!");
    }

    private void hapusPengingat() {
        if (pengingatTerpilih == null) {
            showAlert("Pilih pengingat yang ingin dihapus.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus Pengingat");
        confirm.setContentText("Apakah Anda yakin ingin menghapus pengingat ini?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            pengingatList.remove(pengingatTerpilih);
            resetForm();
            updateStatistik();
            statusLabel.setText("Pengingat berhasil dihapus!");
        }
    }

    private void resetForm() {
        transaksiComboBox.getSelectionModel().clearSelection();
        resetDetailTransaksi();
        tanggalPengingatPicker.setValue(null);
        jamComboBox.setValue("08");
        menitComboBox.setValue("00");
        pengulanganComboBox.setValue("Sekali");
        catatanTextArea.clear();
        pengingatTerpilih = null;
        statusLabel.setText("Form direset. Pilih transaksi untuk membuat pengingat.");
    }

    private void refreshData() {
        loadData();
        updateStatistik();
        statusLabel.setText("Data berhasil direfresh!");
    }

    private void pilihPengingat(PengingatTransaksi pengingat) {
        pengingatTerpilih = pengingat;

        // Set form sesuai data pengingat terpilih
        // Cari transaksi yang sesuai di ComboBox
        for (int i = 0; i < semuaTransaksi.size(); i++) {
            Transaksi t = semuaTransaksi.get(i);
            if (t.getDeskripsi().equals(pengingat.getNamaTransaksi()) &&
                    t.getJumlah() == pengingat.getJumlah()) {
                transaksiComboBox.getSelectionModel().select(i);
                break;
            }
        }

        tanggalPengingatPicker.setValue(pengingat.getTanggalPengingat());

        String[] waktuParts = pengingat.getWaktu().split(":");
        if (waktuParts.length == 2) {
            jamComboBox.setValue(waktuParts[0]);
            menitComboBox.setValue(waktuParts[1]);
        }

        pengulanganComboBox.setValue(pengingat.getPengulangan());
        catatanTextArea.setText(pengingat.getCatatan());

        statusLabel.setText("Pengingat dipilih: " + pengingat.getNamaTransaksi());
    }

    private void filterPengingat() {
        // Filter berdasarkan checkbox tampilkan semua
        if (tampilkanSemuaCheckBox.isSelected()) {
            // Tampilkan semua pengingat
            pengingatTableView.setItems(pengingatList);
        } else {
            // Hanya tampilkan pengingat aktif
            ObservableList<PengingatTransaksi> aktifList = FXCollections.observableArrayList();
            for (PengingatTransaksi p : pengingatList) {
                if ("Aktif".equals(p.getStatus())) {
                    aktifList.add(p);
                }
            }
            pengingatTableView.setItems(aktifList);
        }
        updateStatistik();
    }

    private void updateStatistik() {
        int total = pengingatList.size();
        int aktif = 0;
        int selesai = 0;

        for (PengingatTransaksi p : pengingatList) {
            if ("Aktif".equals(p.getStatus())) {
                aktif++;
            } else if ("Selesai".equals(p.getStatus())) {
                selesai++;
            }
        }

        totalPengingatLabel.setText("Total Pengingat: " + total);
        pengingatAktifLabel.setText("Aktif: " + aktif);
        pengingatSelesaiLabel.setText("Selesai: " + selesai);
    }

    private void tandaiSelesai() {
        // Implementasi tandai selesai untuk notifikasi
        tutupNotifikasi();
        statusLabel.setText("Pengingat ditandai selesai!");
    }

    private void tundaPengingat() {
        // Implementasi tunda pengingat
        tutupNotifikasi();
        statusLabel.setText("Pengingat ditunda!");
    }

    private void tutupNotifikasi() {
        notifikasiPanel.setVisible(false);
    }

    private boolean validasiForm() {
        if (transaksiComboBox.getSelectionModel().getSelectedIndex() < 0) {
            showAlert("Pilih transaksi terlebih dahulu!");
            return false;
        }

        if (tanggalPengingatPicker.getValue() == null) {
            showAlert("Pilih tanggal pengingat!");
            return false;
        }

        if (jamComboBox.getValue() == null || menitComboBox.getValue() == null) {
            showAlert("Pilih waktu pengingat!");
            return false;
        }

        if (pengulanganComboBox.getValue() == null) {
            showAlert("Pilih jenis pengulangan!");
            return false;
        }

        return true;
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

