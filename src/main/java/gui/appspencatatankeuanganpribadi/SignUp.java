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

public class SignUp {
    @FXML
    private TextField signupUsernameField;

    @FXML
    private PasswordField signupPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button createAccountButton;

    public void initialize() {
        // Add action handlers if not already set in FXML
        if (createAccountButton != null) {
            createAccountButton.setOnAction(event -> handleCreateAccount());
        }
    }

    @FXML
    private void handleCreateAccount() {
        String username = signupUsernameField.getText();
        String password = signupPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Basic validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Passwords do not match.");
            return;
        }

        // Register user
        if (DBManager.registerUser(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                    "Account created successfully. You can now log in.");
            navigateToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Error",
                    "Could not create account. Username may already exist.");
        }
    }

    @FXML
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) signupUsernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not navigate to login page.");
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