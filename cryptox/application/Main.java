package application;
	
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
	    
	    Security.addProvider(new BouncyCastleProvider());
	    
        try {
            System.out.println(getClass());
            Parent root = FXMLLoader.load(getClass().getResource("cryptox.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("cryptox.");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
	public static void main(String[] args) {
		launch(args);
	}
}
