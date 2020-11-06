package viewControllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import dataBaseHandling.OperacoesDB;
import entities.Vaga;
import exceptionHandling.TelaDeExcecoes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import viewControllers.observers.Observador;

public class GerenciarVagasController implements Initializable {
	Vaga vaga;

	@FXML
	CheckBox reservarVaga;
	@FXML
	Label numeroDaVaga;
	@FXML
	Label situacao;
	@FXML
	Label valorDaVaga;
	@FXML
	ListView<Vaga> vagasCadastradas;
	@FXML
	TextArea detalhes;

	@FXML
	private void vagasCadastradasAction(Event event) {
		povoarCampos();
	}

	@FXML
	private void venderIngressoAction(Event event) {
		if (conferirCampos()) {
			if (vaga.getSituacao().equals("vendida")) {
				TelaDeExcecoes.lancarExcecao(AlertType.WARNING, "Atenção!", "Esta vaga já foi vendida",
						"Clique em cancelar venda para a tornar disponível novamente");
			} else {
				Observador.objeto = vaga;
				MainController.at.abrirNovaJanela("Vender Ingresso", "/views/VenderIngressoView.fxml");
			}
		}
	}

	@FXML
	private void cancelarVendaAction(Event event) {
		if (conferirCampos() && TelaDeExcecoes.lancarExcecao(AlertType.CONFIRMATION, "Atenção!",
				"Você irá cancelar a venda de uma vaga.", "Deseja continuar?").get().equals(ButtonType.OK)) {
			if (situacao.getText().equals("vendida")) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String pessoaVinculada = vaga.getNumero_pessoa();
				vaga.setNumero_pessoa(null);
				vaga.setSituacao("disponivel");
				vaga.addDetalhes(String.format("Essa vaga foi cancelada em %s.", sdf.format(new Date())));
				OperacoesDB.getEventos().forEach(x -> {
					if (x.getNome().equals(Observador.nomeDoEventoAtual.getText())) {
						vaga.setValor(x.getValor_vaga());
					}
				});
				OperacoesDB.atualizarVaga(vaga);
				OperacoesDB.getPessoas().forEach(x -> {
					if (x.getNumero().equals(pessoaVinculada)) {
						x.addHistorico(String.format("A compra da vaga para o evento \"%s\" foi cancelada em %s.",
								vaga.getNome_evento() ,sdf.format(new Date())));
						OperacoesDB.atualizarPessoa(x);
					}
				});
			}
		}
		atualizar();
		vagasCadastradas.getSelectionModel().select(vaga);
		povoarCampos();
	}

	@FXML
	private void limparAlteracoesAction(Event event) {
		vaga = null;
		atualizar();
		povoarCampos();
	}

	@FXML
	private void salvarAlteracoesAction(Event event) {
		atualizar();
		if (conferirCampos()) {
			if (TelaDeExcecoes
					.lancarExcecao(AlertType.CONFIRMATION, "Confirme essa ação",
							"Você irá atualizar as informações da vaga.", "Essa ação atualizará a vaga selecionada.")
					.get().equals(ButtonType.OK)) {
				if (!vaga.getSituacao().equals("vendida")) {
					vaga.setSituacao(reservarVaga.isSelected() ? "reservada" : "disponivel");
					vaga.setDetalhes(detalhes.getText());
					OperacoesDB.atualizarVaga(vaga);
				} else if (!reservarVaga.isSelected()) {
					vaga.setDetalhes(detalhes.getText());
					OperacoesDB.atualizarVaga(vaga);
				} else {
					TelaDeExcecoes.lancarExcecao(AlertType.INFORMATION, "Atenção!", "A vaga não foi atulizada",
							"Vagas vendidas não podem ser reservadas.");
				}
			}
		}
		atualizar();
		vagasCadastradas.getSelectionModel().select(vaga);
		povoarCampos();
	}

	private void atualizar() {
		if (Observador.nomeDoEventoAtual != null) {
			ObservableList<Vaga> dados = FXCollections
					.observableList(OperacoesDB.getVagasPorNomeEvento(Observador.nomeDoEventoAtual.getText()));
			vagasCadastradas.setItems(dados);
		} else {
			povoarCampos();
		}
	}

	private void povoarCampos() {
		if (vagasCadastradas.getSelectionModel().getSelectedItem() != null) {
			vaga = vagasCadastradas.getSelectionModel().getSelectedItem();

			reservarVaga.setSelected(vaga.getSituacao().equals("reservada"));
			numeroDaVaga.setText(vaga.getNumero_vaga().toString());
			situacao.setText(vaga.getSituacao());
			valorDaVaga.setText(vaga.getValor().toString());
			detalhes.setText(vaga.getDetalhes());
		} else {
			reservarVaga.setSelected(false);
			numeroDaVaga.setText("");
			situacao.setText("");
			valorDaVaga.setText("");
			detalhes.setText("");
		}
	}

	/*
	 * retorna verdadeiro se os campos verificados estiverem preenchidos, se não
	 * estiverem ele mostrará uma aviso e retornará falso
	 */
	private boolean conferirCampos() {
		if (numeroDaVaga.getText().isBlank()) {
			TelaDeExcecoes.lancarExcecao(AlertType.INFORMATION, "Atenção!", "Não há vagas selecionadas.",
					"Selecione uma vaga para realizar essa ação.");
			return false;
		}
		return true;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		atualizar();
	}
}
