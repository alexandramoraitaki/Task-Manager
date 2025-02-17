import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private TaskManager taskManager = TaskManager.getInstance();

    @Override
    public void start(Stage primaryStage) {
        try {
            // Φόρτωση του Scene Builder FXML
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Main.fxml"));
            Parent root = loader.load();

            // Δημιουργία και εμφάνιση της σκηνής
            Scene scene = new Scene(root);
            primaryStage.setTitle("Task Manager");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        taskManager.saveToJSON();
        System.out.println("Data saved to JSON.");
    }

    public static void main(String[] args) {
        launch(args);
    }


}
