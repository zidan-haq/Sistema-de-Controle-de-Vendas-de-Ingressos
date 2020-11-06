package application;

import java.util.Locale;

import exceptionHandling.TelaDeExcecoes;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Main extends Application {
	public static Stage stage;
	
	@Override
	public void start(Stage primaryStage) {
		Locale.setDefault(new Locale("pt", "BR"));
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
			stage = primaryStage;
			
			primaryStage.setScene(new Scene(root));
			primaryStage.setTitle("Sistema de venda de ingressos");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "Exception", e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
