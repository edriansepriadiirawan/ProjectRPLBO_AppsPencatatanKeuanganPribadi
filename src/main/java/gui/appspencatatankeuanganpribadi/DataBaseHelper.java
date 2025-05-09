package gui.appspencatatankeuanganpribadi;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper {
    private static final String URL = "jdbc:sqlite:src/main/resources/keuangan.db";

    // Membuat koneksi
    public static Connection connect() throws SQLException {
        System.out.println("[DEBUG] Lokasi database: " + new java.io.File("keuangan.db").getAbsolutePath());
        return DriverManager.getConnection(URL);
    }

    // Inisialisasi tabel user
    public static void initUserTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                username TEXT PRIMARY KEY,
                password TEXT NOT NULL
            );
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean validateLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            boolean found = rs.next();
            System.out.println("Login dicoba: " + username + " / " + password + " → " + (found ? "BERHASIL" : "GAGAL"));
            return found;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            System.out.println("User berhasil disimpan: " + username + " / " + password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Tambahkan di dalam DatabaseHelper class
    public static void initTransaksiTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS transaksi (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            tanggal TEXT NOT NULL,
            deskripsi TEXT,
            kategori TEXT,
            tipe TEXT,
            jumlah REAL
        );
    """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void simpanTransaksi(Transaksi transaksi) {
        String sql = "INSERT INTO transaksi(tanggal, deskripsi, kategori, tipe, jumlah) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transaksi.getTanggal().toString());
            stmt.setString(2, transaksi.getDeskripsi());
            stmt.setString(3, transaksi.getKategori());
            stmt.setString(4, transaksi.getTipe());
            stmt.setDouble(5, transaksi.getJumlah());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Transaksi> ambilSemuaTransaksi() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal DESC";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDate tanggal = LocalDate.parse(rs.getString("tanggal"));
                String deskripsi = rs.getString("deskripsi");
                String kategori = rs.getString("kategori");
                String tipe = rs.getString("tipe");
                double jumlah = rs.getDouble("jumlah");
                list.add(new Transaksi(tanggal, deskripsi, kategori, tipe, jumlah));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public static void initKategoriTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS kategori (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nama TEXT UNIQUE NOT NULL
        );
    """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("[DEBUG] Tabel kategori disiapkan.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void tambahKategori(String nama) {
        String sql = "INSERT OR IGNORE INTO kategori(nama) VALUES(?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama);
            stmt.executeUpdate();
            System.out.println("[DEBUG] Kategori ditambah: " + nama);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void hapusKategori(String nama) {
        String sql = "DELETE FROM kategori WHERE nama = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama);
            stmt.executeUpdate();
            System.out.println("[DEBUG] Kategori dihapus: " + nama);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getSemuaKategori() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT nama FROM kategori ORDER BY nama ASC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("nama"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void hapusTransaksi(Transaksi transaksi) {
        String sql = """
        DELETE FROM transaksi
        WHERE tanggal = ? AND deskripsi = ? AND kategori = ? AND tipe = ? AND jumlah = ?
        LIMIT 1
    """;
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transaksi.getTanggal().toString());
            stmt.setString(2, transaksi.getDeskripsi());
            stmt.setString(3, transaksi.getKategori());
            stmt.setString(4, transaksi.getTipe());
            stmt.setDouble(5, transaksi.getJumlah());
            stmt.executeUpdate();
            System.out.println("[DEBUG] Transaksi dihapus: " + transaksi);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double getTotalByTipe(String tipe) {
        String sql = "SELECT SUM(jumlah) FROM transaksi WHERE tipe = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipe);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }


}
