package gui.appspencatatankeuanganpribadi;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import static java.sql.DriverManager.getConnection;

public class DataBaseHelper {
    private static final String URL = "jdbc:sqlite:src/main/resources/keuangan.db";

    // === Koneksi ===
    public static Connection connect() throws SQLException {
        System.out.println("[DEBUG] Lokasi database: " + new File("keuangan.db").getAbsolutePath());
        return getConnection(URL);
    }

    // === USER ===
    public static void initUserTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                username TEXT PRIMARY KEY,
                password TEXT NOT NULL
            );
        """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    public static boolean validateLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username); stmt.setString(2, password);
            boolean found = stmt.executeQuery().next();
            System.out.println("Login dicoba: " + username + " → " + (found ? "BERHASIL" : "GAGAL"));
            return found;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    public static void saveUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username); stmt.setString(2, password);
            stmt.executeUpdate();
            System.out.println("User berhasil disimpan: " + username);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // === TRANSAKSI ===
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
        } catch (SQLException e) { e.printStackTrace(); }
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
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Transaksi> ambilSemuaTransaksi() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal DESC";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Transaksi(
                        LocalDate.parse(rs.getString("tanggal")),
                        rs.getString("deskripsi"),
                        rs.getString("kategori"),
                        rs.getString("tipe"),
                        rs.getDouble("jumlah"),
                        rs.getInt("id")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void hapusTransaksi(Transaksi transaksi) {
        String sql = """
        DELETE FROM transaksi
        WHERE tanggal = ? AND deskripsi = ? AND kategori = ? AND tipe = ? AND jumlah = ?
        LIMIT 1;
        """;
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transaksi.getTanggal().toString());
            stmt.setString(2, transaksi.getDeskripsi());
            stmt.setString(3, transaksi.getKategori());
            stmt.setString(4, transaksi.getTipe());
            stmt.setDouble(5, transaksi.getJumlah());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void hapusTransaksiBerdasarkanId(int id) {
        String sql = "DELETE FROM transaksi WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static double getTotalByTipeDanBulan(String tipe, YearMonth bulan) {
        double total = 0;
        String sql = "SELECT SUM(jumlah) FROM transaksi WHERE tipe = ? AND strftime('%Y-%m', tanggal) = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipe);
            pstmt.setString(2, bulan.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) total = rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return total;
    }

    public static double getTotalByTipe(String tipe) {
        double total = 0;
        String sql = "SELECT SUM(jumlah) FROM transaksi WHERE tipe = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipe);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) total = rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return total;
    }

    public static double getTotalSaldo() {
        return getTotalByTipe("Pemasukan") - getTotalByTipe("Pengeluaran");
    }

    public static List<Transaksi> getTransaksiTerbaru(int limit) {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT * FROM transaksi ORDER BY tanggal DESC LIMIT ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Transaksi(
                        LocalDate.parse(rs.getString("tanggal")),
                        rs.getString("deskripsi"),
                        rs.getString("kategori"),
                        rs.getString("tipe"),
                        rs.getDouble("jumlah"),
                        rs.getInt("id")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static Map<String, Double> getJumlahPerKategoriAllTipe(YearMonth bulan) {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT kategori, tipe, SUM(jumlah) FROM transaksi WHERE strftime('%Y-%m', tanggal) = ? GROUP BY kategori, tipe";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bulan.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String kategori = rs.getString("kategori");
                double jumlah = rs.getDouble(3);
                data.put(kategori + " ", jumlah);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    // === KATEGORI ===
    public static void initKategoriTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS kategori (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nama TEXT UNIQUE NOT NULL,
            jenis TEXT NOT NULL,
            ikon TEXT
        );
        """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("[DEBUG] Tabel kategori disiapkan.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void tambahKategoriBaru(String nama, String jenis, String ikon) {
        String sql = "INSERT INTO kategori(nama, jenis, ikon) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama);
            stmt.setString(2, jenis);
            stmt.setString(3, ikon);
            stmt.executeUpdate();
            System.out.println("[DEBUG] Kategori baru ditambahkan: " + nama);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateKategori(int id, String nama, String jenis, String ikon) {
        String sql = "UPDATE kategori SET nama = ?, jenis = ?, ikon = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama);
            stmt.setString(2, jenis);
            stmt.setString(3, ikon);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            System.out.println("[DEBUG] Kategori diupdate: ID " + id);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void hapusKategoriById(int id) {
        String sql = "DELETE FROM kategori WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("[DEBUG] Kategori dihapus ID: " + id);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Kategori> getSemuaKategoriObjek() {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori ORDER BY nama ASC";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Kategori(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("jenis"),
                        rs.getString("ikon")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
