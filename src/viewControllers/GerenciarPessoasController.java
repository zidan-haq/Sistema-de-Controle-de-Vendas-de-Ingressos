package viewControllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import dataBaseHandling.OperacoesDB;
import entities.Pessoa;
import exceptionHandling.TelaDeExcecoes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import viewControllers.constraints.RestricoesDeDados;

public class GerenciarPessoasController implements Initializable {
	Pessoa pessoa;

	@FXML
	ListView<Pessoa> pessoasCadastradas;
	@FXML
	TextField nome;
	@FXML
	TextField numero;
	@FXML
	TextField email;
	@FXML
	DatePicker nascimento;
	@FXML
	TextArea historico;

	@FXML
	private void pessoasCadastradasAction() {
		povoarCampos();
	}

	@FXML
	private void excluirPessoa() {
		if (conferirCampos() && TelaDeExcecoes.lancarExcecao(AlertType.CONFIRMATION, "Atenção!",
								"Você irá excluir uma pessoa do banco de dados.", "Deseja continuar?")
						.get().equals(ButtonType.OK)) {
			OperacoesDB.excluirPessoaPorNumero(pessoasCadastradas.getSelectionModel().getSelectedItem().getNumero());

			atualizar();
		}
	}

	@FXML
	private void adicionarPessoa() {
		if (conferirCampos()) {
			Calendar calendar = Calendar.getInstance();
			if (nascimento.getValue() != null) {
				Date date = Date.from(nascimento.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
				calendar.setTime(date);
			} else {
				calendar = null;
			}

			Pessoa novaPessoa = new Pessoa(nome.getText(), numero.getText().replaceAll("[^\\d*]", ""), email.getText(),
					calendar, historico.getText());
			
			if (!atualizarPessoa(novaPessoa)) {
				OperacoesDB.adicionarPessoa(novaPessoa);
			}
			atualizar();
		}
	}

	// Se a pessoa já for cadastrada perguntará se deseja atualizar, se sim retornará true.
		private boolean atualizarPessoa(Pessoa novaPessoa) {
			atualizar();
			for (Pessoa x : pessoasCadastradas.getItems()) {
				if (x.getNumero().equals(novaPessoa.getNumero())) {
					if (TelaDeExcecoes.lancarExcecao(AlertType.CONFIRMATION, "Confirme essa ação",
							"Já existe uma pessoa com o número informado.",
							"Essa ação irá atualizar o cadastro existente." + "\nDeseja atualizar?")
							.get().equals(ButtonType.OK)) {
						OperacoesDB.atualizarPessoa(novaPessoa);
					}
					return true;
				}
			}
			return false;
		}
	
	private void atualizar() {
		ObservableList<Pessoa> dados = FXCollections.observableList(OperacoesDB.getPessoas());
		pessoasCadastradas.setItems(dados);
		povoarCampos();
	}

	private void povoarCampos() {
		if (pessoasCadastradas.getSelectionModel().getSelectedItem() != null) {
			pessoa = pessoasCadastradas.getSelectionModel().getSelectedItem();

			nome.setText(pessoa.getNome());
			numero.setText(pessoa.getNumero().toString());
			email.setText(pessoa.getEmail());
			nascimento.setValue(pessoa.getNascimento() == null ? null
					: LocalDate.ofInstant(pessoa.getNascimento().toInstant(),
							pessoa.getNascimento().getTimeZone().toZoneId()));
			historico.setText(pessoa.getHistorico());
			RestricoesDeDados.formatarTextField(numero, numero.getText());
		} else {
			nome.setText("");
			numero.setText("");
			email.setText("");
			nascimento.getEditor().clear();
			historico.setText("");
		}
	}

	/*
	 * retorna verdadeiro se os campos verificados estiverem preenchidos, se não
	 * estiverem ele mostrará uma aviso e retornará falso
	 */
	private boolean conferirCampos() {
		int numeroLimpo = numero.getText().replaceAll("[^\\d*]", "").length();
		if (nome.getText().isBlank() || numero.getText().isBlank() || numero.getText().contains("_") || numeroLimpo < 10) {
			TelaDeExcecoes.lancarExcecao(AlertType.INFORMATION, "Atenção!", "Não foi possível concluir essa ação.",
					"Há campos não preenchidos ou preenchidos incorretamente.");
			return false;
		}
		return true;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		atualizar();
		RestricoesDeDados.restricaoNumeroContato(numero);
	}
}
