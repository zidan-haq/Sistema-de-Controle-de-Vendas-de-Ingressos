package viewControllers;

import java.net.URL;
import java.util.ResourceBundle;

import dataBaseHandling.OperacoesDB;
import entities.Evento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import viewControllers.observers.Observador;
import viewControllers.screenManager.AlteracaoDeTela;

public class MainController implements Initializable {
	static AlteracaoDeTela at = new AlteracaoDeTela();
	ObservableList<Evento> dados;
	int disponiveis;
	int vendidas;
	int reservadas;

	// Esses são os componentes do AnchorPane da tela principal
	@FXML
	private ComboBox<Evento> selecioneUmEvento;
	@FXML
	private PieChart graficoDePizza;
	@FXML
	private Label quantidadeDeVagas;
	@FXML
	private Label vagasDisponiveis;
	@FXML
	private Label vagasVendidas;
	@FXML
	private Label vagasReservadas;
	
	// Esse é o label da coluna controle de ingressos
	@FXML Label eventoAtual;
	// Esses são os botões da coluna controle de ingressos
	@FXML
	private void telaPrincipalAction(Event event) {
		at.AlterarParaTelaPrincipal();
	}

	@FXML
	private void gerenciarVagasAction(Event event) {
		at.AlterarTela("/views/GerenciarVagasView.fxml", "9c4c44");
	}

	@FXML
	private void gerenciarEventosAction(Event event) {
		at.AlterarTela("/views/GerenciarEventosView.fxml", "28802a");
	}

	@FXML
	private void gerenciarPessoasAction(Event event) {
		at.AlterarTela("/views/GerenciarPessoasView.fxml", "602a7d");
	}

	@FXML
	private void detalhesDeVendasAction(Event event) {
		at.AlterarTela("/views/DetalhesVendasView.fxml", "111111");
	}

	// Evento do ComboBox
	@FXML
	private void selecioneUmEventoAction(Event event) {
		povoarLabels();
		povoarGrafico();
	}

	private void povoarLabels() {
		disponiveis = 0;
		vendidas = 0;
		reservadas = 0;
		for (Evento x : dados) {
			if(selecioneUmEvento.getSelectionModel().getSelectedItem() == null) {
				break;
			}			
			if (x.getNome().equals(selecioneUmEvento.getSelectionModel().getSelectedItem().getNome())) {	
				quantidadeDeVagas.setText(String.format("%d", x.getQuant_vagas()));
				Observador.nomeDoEventoAtual.setText(x.getNome());
				
				x.getListaDeVagas().forEach(vaga -> {
					if (vaga.getSituacao().equals("disponivel")) {
						disponiveis++;
					} else if (vaga.getSituacao().equals("vendida")) {
						vendidas++;
					} else if (vaga.getSituacao().equals("reservada")) {
						reservadas++;
					}
				});

				vagasDisponiveis.setText(String.format("%d", disponiveis));
				vagasVendidas.setText(String.format("%d", vendidas));
				vagasReservadas.setText(String.format("%d", reservadas));
			}
		}
	}

	private void povoarGrafico() {
		graficoDePizza.getData().clear();
		if (selecioneUmEvento.getSelectionModel().getSelectedItem() != null) {
			graficoDePizza.setTitle(selecioneUmEvento.getSelectionModel().getSelectedItem().getNome());
			graficoDePizza.setLegendVisible(false);
			ObservableList<PieChart.Data> dadosGrafico = FXCollections.observableArrayList(
					new PieChart.Data("Disponiveis", disponiveis), new PieChart.Data("Vendidas", vendidas),
					new PieChart.Data("Reservadas", reservadas));
			graficoDePizza.setData(dadosGrafico);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Observador.setNomeDoEventoSelecionado(eventoAtual);
		dados = FXCollections.observableList(OperacoesDB.getEventos());
		selecioneUmEvento.setItems(dados);
		
		if(Observador.nomeDoEventoAtual != null) {
			selecioneUmEvento.getItems().forEach(x -> {
				if(x.getNome().equals(Observador.nomeDoEventoAtual.getText())) {
					selecioneUmEvento.getSelectionModel().select(x);
				}
			});
		}
		
		povoarLabels();
		povoarGrafico();
	}
}
