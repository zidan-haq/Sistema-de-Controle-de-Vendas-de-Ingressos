package dataBaseHandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import exceptionHandling.TelaDeExcecoes;
import javafx.scene.control.Alert.AlertType;

public class ConexaoDB {
	private static Connection conn = null;
	
	public static Connection conectar() {
		try {
			if(conn == null) {
				conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/sistema_de_ingressos", "root", "");
			}
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
		return conn;
	}
	
	public static void desconectar() {
		try {
			conn.close();
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
	}
}
