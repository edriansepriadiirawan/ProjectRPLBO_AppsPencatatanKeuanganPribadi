package gui.appspencatatankeuanganpribadi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class Login {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    // User tab fields
    @FXML
    private TextField userUsernameField;

    @FXML
    private PasswordField userPasswordField;

    public void initialize() {
        // Initialize database when application starts
        DBManager.initializeDatabase();

        // Add action handlers if not already set in FXML
        if (loginButton != null) {
            loginButton.setOnAction(event -> handleLogin());
        }

        if (signupButton != null) {
            signupButton.setOnAction(event -> navigateToSignup());
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password.");
            return;
        }

        if (DBManager.authenticateUser(username, password)) {
            // Start user session
            SessionManager.startSession(username);

            // Navigate to dashboard
            openDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password.");
        }
    }

    @FXML
    private void handleUserLogin() {
        String username = userUsernameField.getText();
        String password = userPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password.");
            return;
        }

        if (DBManager.authenticateUser(username, password)) {
            // Start user session
            SessionManager.startSession(username);

            // Navigate to dashboard
            openDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password.");
        }
    }

    @FXML
    private void navigateToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) signupButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open signup page.");
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            // Get the controller and initialize dashboard data
            DashboardController dashboardController = loader.getController();
            dashboardController.refreshDashboard(); // Will be implemented in DashboardController

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());
            stage.setTitle("Personal Finance Dashboard - " + SessionManager.getCurrentUsername());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open dashboard.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}