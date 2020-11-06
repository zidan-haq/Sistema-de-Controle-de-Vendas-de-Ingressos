package viewControllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

import dataBaseHandling.OperacoesDB;
import entities.Pessoa;
import entities.Vaga;
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
import viewControllers.observers.Observador;

public class VenderParaCadastradosController implements Initializable {
	Pessoa pessoa;
	Vaga vaga;

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
	private void voltarAction() {
		MainController.at.abrirNovaJanela("Vender Ingresso", "/views/VenderIngressoView.fxml");
	}

	@FXML
	private void venderAction() {
		if (conferirCampos() && TelaDeExcecoes.lancarExcecao(AlertType.CONFIRMATION, "Confirme essa ação",
				"Você irá vender um ingresso", "Deseja continuar?").get().equals(ButtonType.OK)) {
			vincularPessoaVaga(pessoa);
		}
	}

	private void vincularPessoaVaga(Pessoa novaPessoa) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		novaPessoa.addHistorico(String.format("Comprou um ingresso para o evento \"%s\" no valor de R$ %.2f no dia %s.",
				vaga.getNome_evento(), vaga.getValor(), sdf.format(new Date())));

		vaga.setNumero_pessoa(novaPessoa.getNumero());
		vaga.addDetalhes(String.format("Essa vaga foi vendida para \"%s\" no dia %s. Número para contato: %s.",
				novaPessoa.getNome(), sdf.format(new Date()), novaPessoa.getNumero()));

		OperacoesDB.atualizarPessoa(novaPessoa);
		OperacoesDB.atualizarVaga(vaga);
		Observador.objeto = null; // liberando a variável objeto do observador;
		TelaDeExcecoes.lancarExcecao(AlertType.INFORMATION, "Informação", "Operação realizada com sucesso.",
				"Você acabou de vender um ingresso.");
		sair();
	}

	private void atualizar() {
		ObservableList<Pessoa> dados = FXCollections.observableList(OperacoesDB.getPessoas());
		pessoasCadastradas.setItems(dados);
		povoarCampos();
	}
	
	private void sair() {
		MainController.at.fecharNovaJanela();
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
		if (nome.getText().isBlank() || numero.getText().isBlank()) {
			TelaDeExcecoes.lancarExcecao(AlertType.INFORMATION, "Atenção!", "Há campos não preenchidos.",
					"Preencha os campos Nome e Numero.");
			return false;
		}
		return true;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		vaga = (Vaga) Observador.objeto;
		atualizar();
		RestricoesDeDados.restricaoNumeroContato(numero);
	}
}
