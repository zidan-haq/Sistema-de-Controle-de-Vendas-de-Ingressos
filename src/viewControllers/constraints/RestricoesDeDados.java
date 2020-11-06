package viewControllers.constraints;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class RestricoesDeDados {
	public static void restricaoValoresInt(TextField txt) {
		txt.textProperty().addListener((obs, valorAntigo, novoValor) -> {
			if (novoValor == null || !novoValor.matches("\\d*")) {
				if (novoValor.length() == 1) {
					txt.setText("");
				} else {
					txt.setText(valorAntigo);
				}
			}
		});
	}

	public static void restricaoValoresDouble(TextField txt) {
		txt.textProperty().addListener((obs, valorAntigo, novoValor) -> {
			if (novoValor == null || !novoValor.matches("\\d*([\\.]\\d{0,2})?")) {
				if (!novoValor.contains(".") && novoValor.contains(",")) {
					txt.setText(novoValor.replace(",", "."));
				} else if (novoValor.length() == 1) {
					txt.setText("");
				} else {
					txt.setText(valorAntigo);
				}
			}
		});
	}

	public static void restricaoCaracteres(TextInputControl txt, int tamanho) {
		txt.textProperty().addListener((obs, valorAntigo, novoValor) -> {
			if (novoValor != null) {
				if (novoValor.length() > tamanho) {
					txt.setText(valorAntigo.replace(",", ""));
				}
			}
		});
	}

	private static boolean primeiraVezTextField = true;

	public static void restricaoNumeroContato(TextField txt) {
		txt.textProperty().addListener((obs, valorAntigo, novoValor) -> {
			txt.setOnKeyTyped((KeyEvent event) -> {
				try {
					if (event.getCharacter().matches("\\d{0,1}\\w{0,1}\\s{0,1}\\n{0,1}")) {
						formatarTextField(txt, novoValor);
					} else if (!event.getCharacter().equals(KeyCode.BACK_SPACE.getChar())
							&& !event.getCharacter().equals(KeyCode.DELETE.getChar())) {
						txt.setText(novoValor.replace(event.getCharacter(), ""));
					}
				} catch (StringIndexOutOfBoundsException e) {
				}
			});
		});
	}

	public static void formatarTextField(TextField txt, String novoValor) {
		MascaraTextField mascara = new MascaraTextField(txt, "0123456789");

		if (txt.getText().charAt(0) == '0') {
			mascara.setMask("#### ### ####");
			mascara.formatter();
		} else if (novoValor.replaceAll("[^\\d*]", "").length() < 11) {
			mascara.setMask("(##) ####-####");
			mascara.formatter();
		} else {
			mascara.setMask("(##) #####-####");
			mascara.formatter();
		}
	}
}
