package viewControllers;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import dataBaseHandling.OperacoesDB;
import entities.Pessoa;
import entities.Vaga;
import exceptionHandling.TelaDeExcecoes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import viewControllers.constraints.RestricoesDeDados;
import viewControllers.observers.Observador;

public class VenderIngressoController implements Initializable {
	Vaga vaga;
	
	// Label cabeçalho
	@FXML Label nome_evento;
	
	// esses elementos se referem a parte que trata das vagas
	@FXML Label numero_vaga;
	@FXML Label situacao_vaga;
	@FXML Label valor_vaga;
	@FXML TextField desconto;
	@FXML Label valor_total;
	@FXML TextArea detalhes_vaga;
	
	//esses elementos se referem a parte que trata das pessoas
	@FXML TextField nome_pessoa;
	@FXML TextField email_pessoa;
	@FXML TextField numero_pessoa;
	@FXML DatePicker nascimento_pessoa;
	
	@FXML private void cadastradosAction() {
		MainController.at.abrirNovaJanela("Vender para Cadastrados", "/views/VenderParaCadastradosView.fxml");
	}

	@FXML private void venderAction() {
		if(TelaDeExcecoes.lancarExcecao(AlertType.CONFIRMATION, "Confirme essa ação", "Você irá vender um ingresso",
				"Deseja continuar?").get().equals(ButtonType.OK)) {
			adicionarPessoaEVincularVaga();
		}
	}
	
	private void calcularValor_total() {
		desconto.textProperty().addListener((obs, valorAntigo, novoValor) -> {			
			valorAntigo = valorAntigo != null ? valorAntigo : "0";
			novoValor = desconto.getText().isEmpty() ? "0" : novoValor;

			BigDecimal bdValor_vaga = new BigDecimal(valor_vaga.getText());
			BigDecimal bdDesconto = new BigDecimal(desconto.getText().isBlank() ? "0" : desconto.getText());
			
			if(Double.parseDouble(novoValor) > vaga.getValor()) {
				desconto.setText(valorAntigo);
			} else {
				String resultado = String.valueOf(bdValor_vaga.subtract(bdDesconto));
				valor_total.setText(resultado);
			}
		});
	}
	
	private void adicionarPessoaEVincularVaga() {
		if (conferirCampos()) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Calendar calendar = Calendar.getInstance();
			if (nascimento_pessoa.getValue() != null) {
				Date date = Date.from(nascimento_pessoa.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
				calendar.setTime(date);
			} else {
				calendar = null;
			}

			Pessoa novaPessoa = new Pessoa(nome_pessoa.getText(), numero_pessoa.getText().replaceAll("[^\\d*]", ""),
					email_pessoa.getText(), calendar);
			novaPessoa.addHistorico(String.format("Comprou um ingresso para o evento \"%s\" no valor de R$ %.2f no dia %s.",
					nome_evento.getText(), Double.parseDouble(valor_total.getText()), sdf.format(new Date())));
			
			vaga.setNumero_pessoa(novaPessoa.getNumero());
			vaga.setSituacao("vendida");
			vaga.setValor(Double.parseDouble(valor_total.getText()));

			if (!vincularPessoaVaga(novaPessoa)) {
				vaga.addDetalhes(String.format("Essa vaga foi vendida para \"%s\" no dia %s. Número para contato: %s.", 
						novaPessoa.getNome(), sdf.format(new Date()), novaPessoa.getNumero()));
				OperacoesDB.adicionarPessoa(novaPessoa);
				OperacoesDB.atualizarVaga(vaga);
				Observador.objeto = null; // liberando a variável objeto do observador;
				TelaDeExcecoes.lancarExcecao(AlertType.INFORMATION, "Informação", "Operação realizada com sucesso.",
						"Você acabou de vender um ingresso.");
			}
			sair();
		}
	}

	// Se a pessoa já for cadastrada perguntará se deseja atualizar, se sim retornará true.
		private boolean vincularPessoaVaga(Pessoa novaPessoa) {
			for (Pessoa x : OperacoesDB.getPessoas()) {
				if (x.getNumero().equals(novaPessoa.getNumero())) {
					TelaDeExcecoes.lancarExcecao(AlertType.INFORMATION, "Informação", "Já existe uma pessoa com o número informado.",
							"Você será redirecionado(a) para a tela de cadastrados.");
					cadastradosAction();
					return true;
				}
			}
			return false;
		}
	
	private void sair() {
		MainController.at.fecharNovaJanela();
	}

	/*
	 * retorna verdadeiro se os campos verificados estiverem preenchidos, se não
	 * estiverem ele mostrará uma aviso e retornará falso
	 */
	private boolean conferirCampos() {
		int numeroLimpo = numero_pessoa.getText().replaceAll("[^\\d*]", "").length();
		if (nome_pessoa.getText().isBlank() || numero_pessoa.getText().isBlank() ||
				numero_pessoa.getText().contains("_") ||numeroLimpo < 10) {
			TelaDeExcecoes.lancarExcecao(AlertType.INFORMATION, "Atenção!", "Não foi possível concluir essa ação.",
					"Há campos não preenchidos ou preenchidos incorretamente.");
			return false;
		}
		return true;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		vaga = (Vaga) Observador.objeto;
		nome_evento.setText(Observador.nomeDoEventoAtual.getText());
		numero_vaga.setText(vaga.getNumero_vaga().toString());
		situacao_vaga.setText(vaga.getSituacao());
		valor_vaga.setText(vaga.getValor().toString());
		detalhes_vaga.setText(vaga.getDetalhes());
		valor_total.setText(valor_vaga.getText());
		
		RestricoesDeDados.restricaoValoresDouble(desconto);
		RestricoesDeDados.restricaoCaracteres(desconto, 7);
		RestricoesDeDados.restricaoNumeroContato(numero_pessoa);

		calcularValor_total();
	}
}
