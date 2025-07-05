import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/MainApplication.fxml"));
        primaryStage.setTitle("Topological Sorting Visualizer");
        
        // Увеличиваем размеры основного окна
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

