package exceptionHandling;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class TelaDeExcecoes {
	public static Optional<ButtonType> lancarExcecao(AlertType alertType, String nome, String cabecalho, String conteudo) {
		Alert alerta = new Alert(alertType);
		alerta.setTitle(nome);
		alerta.setHeaderText(cabecalho);
		alerta.setContentText(conteudo);
		alerta.setResizable(true);
		return alerta.showAndWait();
	}
}
