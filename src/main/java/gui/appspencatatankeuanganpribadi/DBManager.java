package gui.appspencatatankeuanganpribadi;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final String DB_URL = "jdbc:sqlite:keuangan_pribadi.db";
    private static Connection connection;

    // Initialize database - create tables if they don't exist
    public static void initializeDatabase() {
        try {
            // Create connection
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established successfully.");

            // Create tables if they don't exist
            createTables();

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Create necessary tables
    private static void createTables() throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "is_admin INTEGER DEFAULT 0," +
                "last_login TIMESTAMP)";

        String createCategoriesTable = "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT UNIQUE NOT NULL," +
                "type TEXT NOT NULL)";  // 'Pemasukan' or 'Pengeluaran'

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "date DATE NOT NULL," +
                "description TEXT NOT NULL," +
                "category_id INTEGER NOT NULL," +
                "type TEXT NOT NULL," +  // 'Pemasukan' or 'Pengeluaran'
                "amount REAL NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users (id)," +
                "FOREIGN KEY (category_id) REFERENCES categories (id))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createCategoriesTable);
            stmt.execute(createTransactionsTable);

            // Insert default admin user if not exists
            if (!userExists("admin")) {
                String insertAdmin = "INSERT INTO users (username, password, is_admin) VALUES ('admin', '1234', 1)";
                stmt.execute(insertAdmin);
            }

            // Insert default categories if not exists
            insertDefaultCategories();
        }
    }

    private static void insertDefaultCategories() throws SQLException {
        String[] incomeCategories = {"Gaji", "Bonus", "Investasi", "Pendapatan Lainnya"};
        String[] expenseCategories = {"Makanan", "Transportasi", "Belanja", "Pendidikan", "Hiburan", "Lainnya"};

        for (String category : incomeCategories) {
            if (!categoryExists(category)) {
                insertCategory(category, "Pemasukan");
            }
        }

        for (String category : expenseCategories) {
            if (!categoryExists(category)) {
                insertCategory(category, "Pengeluaran");
            }
        }
    }

    private static boolean categoryExists(String categoryName) throws SQLException {
        String query = "SELECT COUNT(*) FROM categories WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }

    private static void insertCategory(String name, String type) throws SQLException {
        String query = "INSERT INTO categories (name, type) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.executeUpdate();
        }
    }

    // Check if user exists
    public static boolean userExists(String username) {
        try {
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }

    // Authenticate user
    public static boolean authenticateUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                boolean authenticated = rs.next();

                if (authenticated) {
                    // Update last login time
                    updateLastLogin(username);
                }

                return authenticated;
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            return false;
        }
    }

    // Register new user
    public static boolean registerUser(String username, String password) {
        try {
            if (userExists(username)) {
                return false; // User already exists
            }

            String query = "INSERT INTO users (username, password, last_login) VALUES (?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    // Update last login time
    private static void updateLastLogin(String username) {
        try {
            String query = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }

    // Get user's last login time
    public static String getLastLogin(String username) {
        try {
            String query = "SELECT last_login FROM users WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("last_login");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting last login: " + e.getMessage());
        }
        return "";
    }

    // Check if user is admin
    public static boolean isAdmin(String username) {
        try {
            String query = "SELECT is_admin FROM users WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("is_admin") == 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user is admin: " + e.getMessage());
        }
        return false;
    }

    // Get user ID
    public static int getUserId(String username) {
        try {
            String query = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user ID: " + e.getMessage());
        }
        return -1;
    }

    // Add transaction
    public static boolean addTransaction(int userId, LocalDate date, String description,
                                         String categoryName, String type, double amount) {
        try {
            // Get category ID
            int categoryId = getCategoryId(categoryName);
            if (categoryId == -1) {
                // Category doesn't exist, create it
                insertCategory(categoryName, type);
                categoryId = getCategoryId(categoryName);
            }

            // Insert transaction
            String query = "INSERT INTO transactions (user_id, date, description, category_id, type, amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, date.toString());
                pstmt.setString(3, description);
                pstmt.setInt(4, categoryId);
                pstmt.setString(5, type);
                pstmt.setDouble(6, amount);

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
            return false;
        }
    }

    // Get category ID
    private static int getCategoryId(String categoryName) throws SQLException {
        String query = "SELECT id FROM categories WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    // Get all transactions for a user
    public static List<Transaksi> getUserTransactions(int userId) {
        List<Transaksi> transactions = new ArrayList<>();
        try {
            String query = "SELECT t.date, t.description, c.name as category, t.type, t.amount " +
                    "FROM transactions t " +
                    "JOIN categories c ON t.category_id = c.id " +
                    "WHERE t.user_id = ? " +
                    "ORDER BY t.date DESC";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    LocalDate date = LocalDate.parse(rs.getString("date"));
                    String description = rs.getString("description");
                    String category = rs.getString("category");
                    String type = rs.getString("type");
                    double amount = rs.getDouble("amount");

                    transactions.add(new Transaksi(date, description, category, type, amount));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Get total income for a user in current month
    public static double getCurrentMonthIncome(int userId) {
        try {
            String query = "SELECT SUM(amount) as total FROM transactions " +
                    "WHERE user_id = ? AND type = 'Pemasukan' " +
                    "AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting current month income: " + e.getMessage());
        }
        return 0.0;
    }

    // Get total expense for a user in current month
    public static double getCurrentMonthExpense(int userId) {
        try {
            String query = "SELECT SUM(amount) as total FROM transactions " +
                    "WHERE user_id = ? AND type = 'Pengeluaran' " +
                    "AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting current month expense: " + e.getMessage());
        }
        return 0.0;
    }

    // Calculate current balance
    public static double getCurrentBalance(int userId) {
        try {
            String incomeQuery = "SELECT SUM(amount) as total FROM transactions " +
                    "WHERE user_id = ? AND type = 'Pemasukan'";
            String expenseQuery = "SELECT SUM(amount) as total FROM transactions " +
                    "WHERE user_id = ? AND type = 'Pengeluaran'";

            double totalIncome = 0.0;
            double totalExpense = 0.0;

            try (PreparedStatement pstmt = connection.prepareStatement(incomeQuery)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    totalIncome = rs.getDouble("total");
                }
            }

            try (PreparedStatement pstmt = connection.prepareStatement(expenseQuery)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    totalExpense = rs.getDouble("total");
                }
            }

            return totalIncome - totalExpense;
        } catch (SQLException e) {
            System.err.println("Error calculating current balance: " + e.getMessage());
            return 0.0;
        }
    }

    // Get expense by category for pie chart
    public static List<CategoryExpense> getExpenseByCategory(int userId) {
        List<CategoryExpense> categoryExpenses = new ArrayList<>();
        try {
            String query = "SELECT c.name as category, SUM(t.amount) as total " +
                    "FROM transactions t " +
                    "JOIN categories c ON t.category_id = c.id " +
                    "WHERE t.user_id = ? AND t.type = 'Pengeluaran' " +
                    "GROUP BY c.name";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String category = rs.getString("category");
                    double amount = rs.getDouble("total");
                    categoryExpenses.add(new CategoryExpense(category, amount));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting expense by category: " + e.getMessage());
        }
        return categoryExpenses;
    }

    // Get monthly income and expense for area chart
    public static List<MonthlyFinance> getMonthlyFinances(int userId, int months) {
        List<MonthlyFinance> monthlyFinances = new ArrayList<>();
        try {
            String query = "WITH RECURSIVE dates(date) AS (" +
                    "  SELECT date('now', 'start of month', '-" + (months-1) + " months') " +
                    "  UNION ALL " +
                    "  SELECT date(date, '+1 month') FROM dates " +
                    "  WHERE date < date('now', 'start of month')" +
                    ")" +
                    "SELECT strftime('%Y-%m', dates.date) as month, " +
                    "  (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'Pemasukan' " +
                    "   AND strftime('%Y-%m', date) = strftime('%Y-%m', dates.date)) as income, " +
                    "  (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'Pengeluaran' " +
                    "   AND strftime('%Y-%m', date) = strftime('%Y-%m', dates.date)) as expense " +
                    "FROM dates;";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, userId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String monthStr = rs.getString("month");
                    double income = rs.getDouble("income");
                    double expense = rs.getDouble("expense");

                    // Convert month format to display name
                    String[] parts = monthStr.split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    String monthName = getMonthName(month);

                    monthlyFinances.add(new MonthlyFinance(monthName, income, expense));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly finances: " + e.getMessage());
        }
        return monthlyFinances;
    }

    private static String getMonthName(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"};
        return monthNames[month - 1];
    }

    // Close database connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    // Helper class for category expenses
    public static class CategoryExpense {
        private String category;
        private double amount;

        public CategoryExpense(String category, double amount) {
            this.category = category;
            this.amount = amount;
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }
    }

    // Helper class for monthly finances
    public static class MonthlyFinance {
        private String month;
        private double income;
        private double expense;

        public MonthlyFinance(String month, double income, double expense) {
            this.month = month;
            this.income = income;
            this.expense = expense;
        }

        public String getMonth() {
            return month;
        }

        public double getIncome() {
            return income;
        }

        public double getExpense() {
            return expense;
        }
    }
}