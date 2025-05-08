package gui.appspencatatankeuanganpribadi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Button logoutButton;

    @FXML
    private Button tambahTransaksiBtn;

    @FXML
    private Button kelolaKategoriBtn;

    @FXML
    private Button lihatTransaksiBtn;

    @FXML
    private Button ringkasanKeuanganBtn;

    @FXML
    private Button editTransaksiBtn;

    @FXML
    private Button filterKategoriBtn;

    @FXML
    private Button notifikasiSaldoBtn;

    @FXML
    private Button pengingatTransaksiBtn;

    @FXML
    private AreaChart<String, Number> areaChart;

    @FXML
    private PieChart pieChart;

    @FXML
    private TableView<Transaksi> transactionTable;

    @FXML
    private TableColumn<Transaksi, LocalDate> dateColumn;

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
        initAreaChart();
        initPieChart();
        initTable();
        setupButtonActions();
    }

    private void initAreaChart() {
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Pemasukan");

        incomeSeries.getData().add(new XYChart.Data<>("Jan", 5000000));
        incomeSeries.getData().add(new XYChart.Data<>("Feb", 5500000));
        incomeSeries.getData().add(new XYChart.Data<>("Mar", 4800000));
        incomeSeries.getData().add(new XYChart.Data<>("Apr", 6200000));
        incomeSeries.getData().add(new XYChart.Data<>("Mei", 7500000));

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Pengeluaran");

        expenseSeries.getData().add(new XYChart.Data<>("Jan", 3000000));
        expenseSeries.getData().add(new XYChart.Data<>("Feb", 3200000));
        expenseSeries.getData().add(new XYChart.Data<>("Mar", 2800000));
        expenseSeries.getData().add(new XYChart.Data<>("Apr", 2400000));
        expenseSeries.getData().add(new XYChart.Data<>("Mei", 2500000));

        areaChart.getData().addAll(incomeSeries, expenseSeries);
    }

    private void initPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Makanan", 25),
                new PieChart.Data("Transportasi", 20),
                new PieChart.Data("Belanja", 15),
                new PieChart.Data("Pendidikan", 10),
                new PieChart.Data("Hiburan", 5),
                new PieChart.Data("Lainnya", 25)
        );

        pieChart.setData(pieChartData);
    }

    private void initTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("tipe"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("jumlah"));

        // Contoh data transaksi
        ObservableList<Transaksi> transaksiList = FXCollections.observableArrayList(
                new Transaksi(LocalDate.of(2025, 5, 1), "Gaji Bulanan", "Pendapatan", "Pemasukan", 7500000.0),
                new Transaksi(LocalDate.of(2025, 5, 2), "Belanja Bulanan", "Makanan", "Pengeluaran", 1500000.0),
                new Transaksi(LocalDate.of(2025, 5, 3), "Bensin", "Transportasi", "Pengeluaran", 300000.0),
                new Transaksi(LocalDate.of(2025, 5, 4), "Makan di Restoran", "Makanan", "Pengeluaran", 150000.0),
                new Transaksi(LocalDate.of(2025, 5, 4), "Bonus Proyek", "Pendapatan", "Pemasukan", 2000000.0)
        );

        transactionTable.setItems(transaksiList);
    }

    private void setupButtonActions() {
        tambahTransaksiBtn.setOnAction(event -> {
            // Implementasi untuk menambah transaksi
            System.out.println("Tambah Transaksi diklik");
        });

        kelolaKategoriBtn.setOnAction(event -> {
            // Implementasi untuk mengelola kategori
            System.out.println("Kelola Kategori diklik");
        });

        lihatTransaksiBtn.setOnAction(event -> {
            // Implementasi untuk melihat data transaksi
            System.out.println("Lihat Data Transaksi diklik");
        });

        ringkasanKeuanganBtn.setOnAction(event -> {
            // Implementasi untuk melihat ringkasan keuangan
            System.out.println("Ringkasan Keuangan diklik");
        });

        editTransaksiBtn.setOnAction(event -> {
            // Implementasi untuk mengedit/menghapus transaksi
            System.out.println("Edit/Hapus Transaksi diklik");
        });

        filterKategoriBtn.setOnAction(event -> {
            // Implementasi untuk filter berdasarkan kategori
            System.out.println("Filter Berdasarkan Kategori diklik");
        });

        notifikasiSaldoBtn.setOnAction(event -> {
            // Implementasi untuk notifikasi saldo
            System.out.println("Notifikasi Saldo diklik");
        });

        pengingatTransaksiBtn.setOnAction(event -> {
            // Implementasi untuk pengingat transaksi
            System.out.println("Pengingat Transaksi diklik");
        });

        logoutButton.setOnAction(event -> {
            // Implementasi untuk logout
            System.out.println("Logout diklik");
        });
    }
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void refreshDashboard() {
    }
}