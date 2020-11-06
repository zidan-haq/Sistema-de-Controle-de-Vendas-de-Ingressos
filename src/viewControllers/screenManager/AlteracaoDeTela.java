package viewControllers.screenManager;

import java.io.IOException;

import application.Main;
import exceptionHandling.TelaDeExcecoes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlteracaoDeTela {
	String caminhoAtual = "/views/MainView.fxml";
	Stage novaJanela = new Stage();

	public void AlterarTela(String caminho, String cor) {
		if (!caminhoAtual.equals(caminho)) {
			caminhoAtual = caminho;
			try {
				AnchorPane novaTela = FXMLLoader.load(getClass().getResource(caminho));

				VBox raiz = (VBox) Main.stage.getScene().getRoot();
				SplitPane telaAntiga = (SplitPane) raiz.getChildren().get(0);

				telaAntiga.getItems().set(1, novaTela);

				Pane pane = (Pane) raiz.getChildren().get(1);
				pane.setBackground(new Background(new BackgroundFill(Paint.valueOf(cor), null, null)));

			} catch (IOException e) {
				TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "IOException", e.getMessage());
			}
		}
	}
	
	public void AlterarParaTelaPrincipal() {
		if (!caminhoAtual.equals("/views/MainView.fxml")) {
			caminhoAtual = "/views/MainView.fxml";
			try {
				VBox vbox = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
				SplitPane splitPane = (SplitPane) vbox.getChildren().get(0);
				AnchorPane novaTela = (AnchorPane) splitPane.getItems().get(1);
				
				VBox raiz = (VBox) Main.stage.getScene().getRoot();
				SplitPane telaAntiga = (SplitPane) raiz.getChildren().get(0);

				telaAntiga.getItems().set(1, novaTela);

				Pane pane = (Pane) raiz.getChildren().get(1);
				pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("2960a3"), null, null)));

			} catch (IOException e) {
				TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "IOException", e.getMessage());
			}
		}
	}
	public void abrirNovaJanela(String nome, String caminho) {
		try {
			Parent fxml = FXMLLoader.load(getClass().getResource(caminho));

			novaJanela.setScene(new Scene(fxml));
			novaJanela.setTitle(nome);
			novaJanela.setResizable(true);
			
			if(novaJanela.getOwner() == null) {
				novaJanela.initOwner(Main.stage);
				novaJanela.initModality(Modality.WINDOW_MODAL);
			}
			
			if(!novaJanela.isShowing()) {
				novaJanela.showAndWait();
			}
		} catch (IOException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "IOException", e.getMessage());
		}
	}

	public void fecharNovaJanela() {
		novaJanela.close();
	}
}
