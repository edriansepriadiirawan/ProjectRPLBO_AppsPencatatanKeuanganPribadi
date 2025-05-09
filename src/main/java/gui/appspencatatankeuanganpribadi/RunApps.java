package gui.appspencatatankeuanganpribadi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RunApps extends Application {
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(RunApps.class.getResource("Login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("dashboard.css").toExternalForm());
        stage.setTitle("Hello gesss welkom bek to my canel");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        DataBaseHelper.initUserTable();
        DataBaseHelper.initKategoriTable();
        DataBaseHelper.initTransaksiTable(); // Tambahan baris ini
        launch(args);
    }

}





