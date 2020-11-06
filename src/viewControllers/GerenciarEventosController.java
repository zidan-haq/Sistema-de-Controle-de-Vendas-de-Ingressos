package viewControllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import dataBaseHandling.OperacoesDB;
import entities.Evento;
import entities.Vaga;
import exceptionHandling.TelaDeExcecoes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
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

public class GerenciarEventosController implements Initializable {
	Evento evento;

	// Esses são os textfields, textarea e listview
	@FXML
	private ListView<Evento> eventosCadastrados;
	@FXML
	private TextField nomeDoEvento;
	@FXML
	private TextField quant_vagas;
	@FXML
	private TextField valorDaVaga;
	@FXML
	private DatePicker dataDoEvento;
	@FXML
	private TextArea detalhes;

	// Essa é a ação da seleção de um elemento da lista
	@FXML
	private void eventosCadastradosAction(Event event) {
		povoarCampos();
	}

	// Essas são as ações dos botões
	@FXML
	private void utilizarEventoAction(Event event) {
		if (conferirCampos()) {
			Observador.nomeDoEventoAtual.setText(evento.getNome());
			MainController.at.AlterarParaTelaPrincipal();;
		}
	}

	@FXML
	private void excluirEventoAction(Event event) {
		if (conferirCampos() && TelaDeExcecoes.lancarExcecao(AlertType.CONFIRMATION, "Atenção!",
				"Você irá excluir um evento.", "Deseja continuar?").get().equals(ButtonType.OK)) {
			OperacoesDB.excluirEventoPorNome(eventosCadastrados.getSelectionModel().getSelectedItem().getNome());
			
			atualizar();
		}
	}

	@FXML
	private void adicionarEventoAction(Event event) {
		if (conferirCampos()) {
			Calendar calendar = Calendar.getInstance();
			if (dataDoEvento.getValue() != null) {
				Date date = Date.from(dataDoEvento.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
				calendar.setTime(date);
			} else {
				calendar = null;
			}

			Evento novoEvento = new Evento(nomeDoEvento.getText(), Integer.parseInt(quant_vagas.getText()),
					Double.parseDouble(valorDaVaga.getText().isBlank() ? "0" : valorDaVaga.getText()), calendar,
					detalhes.getText());
			novoEvento.setListaDeVagas(criarListaDeVagas(novoEvento));

			if (!atualizarEvento(novoEvento)) {
				OperacoesDB.adicionarEvento(novoEvento);
			}
			atualizar();
		}
	}

	// Se o evento for existente perguntará se deseja atualizar, se sim retornará true.
	private boolean atualizarEvento(Evento novoEvento) {
		atualizar();
		for (Evento x : eventosCadastrados.getItems()) {
			if (x.getNome().toLowerCase().equals(novoEvento.getNome().toLowerCase())) {
				if (TelaDeExcecoes.lancarExcecao(AlertType.CONFIRMATION, "Confirme essa ação",
						"Já existe um evento com o nome informado.",
						"Essa ação irá apagar todas as vagas existentes." + "\nDeseja atualizar o evento existente?")
						.get().equals(ButtonType.OK)) {
					OperacoesDB.atualizarEvento(novoEvento);
				}
				return true;
			}
		}
		return false;
	}

	private List<Vaga> criarListaDeVagas(Evento evento) {
		List<Vaga> lista = new ArrayList<>();
		for (int x = 1; x <= evento.getQuant_vagas(); x++) {
			lista.add(new Vaga(x, evento.getNome(), "disponivel", evento.getValor_vaga(), "", null));
		}
		return lista;
	}

	private void atualizar() {
		ObservableList<Evento> dados = FXCollections.observableList(OperacoesDB.getEventos());
		eventosCadastrados.setItems(dados);
		povoarCampos();
	}

	private void povoarCampos() {
		if (eventosCadastrados.getSelectionModel().getSelectedItem() != null) {
			evento = eventosCadastrados.getSelectionModel().getSelectedItem();

			nomeDoEvento.setText(evento.getNome());
			quant_vagas.setText(evento.getQuant_vagas().toString());
			valorDaVaga.setText(evento.getValor_vaga().toString());
			dataDoEvento.setValue(evento.getData() == null ? null
					: LocalDate.ofInstant(evento.getData().toInstant(), evento.getData().getTimeZone().toZoneId()));
			detalhes.setText(evento.getDetalhes());
		} else {
			nomeDoEvento.setText("");
			quant_vagas.setText("");
			valorDaVaga.setText("");
			dataDoEvento.getEditor().clear();
			detalhes.setText("");
		}
	}

	/*
	 * retorna verdadeiro se os campos verificados estiverem preenchidos, se não
	 * estiverem ele mostrará uma aviso e retornará falso
	 */
	private boolean conferirCampos() {
		if (nomeDoEvento.getText().isBlank() || quant_vagas.getText().isBlank()) {
			TelaDeExcecoes.lancarExcecao(AlertType.INFORMATION, "Atenção!", "Há campos não preenchidos.",
					"Preencha os campos Nome do evento e Quantidade de vagas.");
			return false;
		}
		return true;
	}

	public void initialize(URL arg0, ResourceBundle arg1) {
		atualizar();

		RestricoesDeDados.restricaoValoresInt(quant_vagas);
		RestricoesDeDados.restricaoCaracteres(quant_vagas, 7);
		RestricoesDeDados.restricaoValoresDouble(valorDaVaga);
		RestricoesDeDados.restricaoCaracteres(valorDaVaga, 7);
		RestricoesDeDados.restricaoCaracteres(detalhes, 255);
	}
}
