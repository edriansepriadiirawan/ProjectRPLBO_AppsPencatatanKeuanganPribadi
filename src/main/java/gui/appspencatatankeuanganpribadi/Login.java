package gui.appspencatatankeuanganpribadi;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class Login {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField signupUsernameField;
    @FXML
    private PasswordField signupPasswordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals("admin") && password.equals("1234")) {
            showAlert("Login Sukses", "Selamat datang, " + username + "!");
        } else {
            showAlert("Login Gagal", "Username atau password salah!");
        }
    }

    @FXML
    private void handleSignup() {
        showAlert("Info", "Silakan isi form di bawah untuk membuat akun baru.");
    }

    @FXML
    private void handleCreateAccount() {
        String username = signupUsernameField.getText();
        String password = signupPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username dan password harus diisi!");
        } else {
            showAlert("Akun Dibuat", "Akun untuk " + username + " berhasil dibuat!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
