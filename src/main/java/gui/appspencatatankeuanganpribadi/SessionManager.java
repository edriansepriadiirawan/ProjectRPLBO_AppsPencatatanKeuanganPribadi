package gui.appspencatatankeuanganpribadi;

public class SessionManager {
    private static String currentUsername;
    private static int currentUserId;
    private static boolean isAdmin;
    private static String lastLogin;

    // Start user session
    public static void startSession(String username) {
        currentUsername = username;
        currentUserId = DBManager.getUserId(username);
        isAdmin = DBManager.isAdmin(username);
        lastLogin = DBManager.getLastLogin(username);
    }

    // End user session
    public static void endSession() {
        currentUsername = null;
        currentUserId = -1;
        isAdmin = false;
        lastLogin = null;
    }

    // Check if a user is logged in
    public static boolean isLoggedIn() {
        return currentUsername != null;
    }

    // Get current username
    public static String getCurrentUsername() {
        return currentUsername;
    }

    // Get current user ID
    public static int getCurrentUserId() {
        return currentUserId;
    }

    // Check if current user is admin
    public static boolean isAdmin() {
        return isAdmin;
    }

    // Get last login time
    public static String getLastLogin() {
        return lastLogin;
    }
}