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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Button logoutButton;

    @FXML
    private Button tambahTransaksiBtn;

    @FXML
    private Button kelolaKategoriBtn;

    @FXML
    private Button editTransaksiBtn;

    @FXML
    private Button lihatTransaksiBtn;

    @FXML
    private Button ringkasanKeuanganBtn;

    @FXML
    private PieChart pieChart;

    @FXML
    private Button filterKategoriBtn;

    @FXML
    private Button notifikasiSaldoBtn;

    @FXML
    private Button pengingatTransaksiBtn;

    @FXML
    private AreaChart<String, Number> areaChart;


    @FXML
    private TableView<Transaksi> transactionTable;

    @FXML
    private TableColumn<Transaksi, LocalDate> dateColumn;
    @FXML
    private Label saldoLabel, pemasukanLabel, pengeluaranLabel;
    @FXML
    private TableView<Transaksi> dashboardTable;

    @FXML
    private TableColumn<Transaksi, String> descriptionColumn;

    @FXML
    private TableColumn<Transaksi, String> categoryColumn;

    @FXML
    private TableColumn<Transaksi, String> typeColumn;


    @FXML
    private TableColumn<Transaksi, Double> amountColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tambahTransaksiBtn.setOnAction(e -> bukaHalaman("Tambah-Transaksi.fxml"));
        editTransaksiBtn.setOnAction(e -> bukaHalaman("EditHapusTransaksi.fxml"));
        lihatTransaksiBtn.setOnAction(e -> bukaHalaman("View-DataTransaksi.fxml"));
        ringkasanKeuanganBtn.setOnAction(e -> bukaHalaman("Ringkasan-Keuangan.fxml"));
        kelolaKategoriBtn.setOnAction(e -> bukaHalaman("Kelola-Kategori.fxml"));
        logoutButton.setOnAction(e -> bukaHalaman("Login.fxml"));
        filterKategoriBtn.setOnAction(event -> bukaHalaman("FilterKategori.fxml"));
        pengingatTransaksiBtn.setOnAction(event -> showAlert("Fitur Belum Tersedia", null, "Pengingat transaksi belum tersedia."));
        dashboardTable.setItems(FXCollections.observableArrayList(DataBaseHelper.ambilSemuaTransaksi()));

        // ✅ Tambahkan ini agar PieChart tampil
        initPieChart();
        initPieChart();
        initAreaChart();
        updateInformasiKeuangan();

        // (opsional) juga panggil yang lain jika ingin langsung tampil:
        initAreaChart();
        initTable();
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

    private void showErrorAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kesalahan");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }


    private void updateInformasiKeuangan() {
        YearMonth bulanIni = YearMonth.now();

        double pemasukanBulan = DataBaseHelper.getTotalByTipeDanBulan("Pemasukan", bulanIni);
        double pengeluaranBulan = DataBaseHelper.getTotalByTipeDanBulan("Pengeluaran", bulanIni);
        double saldo = DataBaseHelper.getTotalSaldo();

        saldoLabel.setText("Rp " + String.format("%,.0f", saldo));
        pemasukanLabel.setText("Rp " + String.format("%,.0f", pemasukanBulan));
        pengeluaranLabel.setText("Rp " + String.format("%,.0f", pengeluaranBulan));
    }

    private void initAreaChart() {
        XYChart.Series<String, Number> pemasukanSeries = new XYChart.Series<>();
        pemasukanSeries.setName("Pemasukan");
        XYChart.Series<String, Number> pengeluaranSeries = new XYChart.Series<>();
        pengeluaranSeries.setName("Pengeluaran");

        for (int i = 1; i <= 5; i++) {
            YearMonth bulan = YearMonth.now().minusMonths(5 - i);
            double pemasukan = DataBaseHelper.getTotalByTipeDanBulan("Pemasukan", bulan);
            double pengeluaran = DataBaseHelper.getTotalByTipeDanBulan("Pengeluaran", bulan);

            pemasukanSeries.getData().add(new XYChart.Data<>(bulan.getMonth().toString().substring(0, 3), pemasukan));
            pengeluaranSeries.getData().add(new XYChart.Data<>(bulan.getMonth().toString().substring(0, 3), pengeluaran));
        }

        areaChart.getData().setAll(pemasukanSeries, pengeluaranSeries);
    }

    private void initPieChart() {
        YearMonth bulan = YearMonth.now();
        Map<String, Double> data = DataBaseHelper.getJumlahPerKategoriAllTipe(bulan);

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            pieData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        pieChart.setData(pieData);
        pieChart.setTitle("Distribusi Pemasukan & Pengeluaran");
    }

    private void initTable() {
        dateColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTanggal()));
        descriptionColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDeskripsi()));
        categoryColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getKategori()));
        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipe()));
        amountColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getJumlah()));

        dashboardTable.setItems(FXCollections.observableArrayList(DataBaseHelper.getTransaksiTerbaru(5)));
    }



    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}