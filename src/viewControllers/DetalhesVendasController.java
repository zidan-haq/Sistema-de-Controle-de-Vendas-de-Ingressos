package viewControllers;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import dataBaseHandling.OperacoesDB;
import entities.Evento;
import entities.Vaga;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

public class DetalhesVendasController implements Initializable {
	ObservableList<Evento> eventos;
	List<Vaga> vagas = new ArrayList<>();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Integer totalVendidas;
	Integer totalDisponiveis;
	Integer totalReservadas;
	BigDecimal totalFaturamento;
	int vendidas;
	int reservadas;
	int disponiveis;

	@FXML
	ComboBox<Evento> selecioneUmEvento;
	@FXML
	Label vagasVendidas;
	@FXML
	Label vagasReservadas;
	@FXML
	Label vagasDisponiveis;
	@FXML
	Label faturamento;
	@FXML
	Label data_evento;
	@FXML
	RadioButton ordenarMaisVendidos;
	@FXML
	RadioButton ordenarQuantidadeVagas;
	@FXML
	RadioButton ordenarMaisProximos;
	@FXML
	BarChart<String, Integer> grafico;

	@FXML
	private void selecaoOrdenarPor(Event event) {
		RadioButton pressionado = (RadioButton) event.getSource();
		ordenarQuantidadeVagas.setSelected(ordenarQuantidadeVagas.equals(pressionado) ? true : false);
		ordenarMaisVendidos.setSelected(ordenarMaisVendidos.equals(pressionado) ? true : false);
		ordenarMaisProximos.setSelected(ordenarMaisProximos.equals(pressionado) ? true : false);
		povoarGrafico();
	}
	
	@FXML
	private void selecioneUmEventoAction(Event event) {
		povoarLabels();
	}
	
	private void calcularTotais() {
		totalVendidas = 0;
		totalReservadas = 0;
		totalDisponiveis = 0;
		totalFaturamento = BigDecimal.ZERO;
		vagas.forEach(x -> {
			totalFaturamento = totalFaturamento.add(new BigDecimal(x.getValor().toString()));
			if (x.getSituacao().equals("vendida")) {
				totalVendidas++;
			} else if (x.getSituacao().equals("reservada")) {
				totalReservadas++;
			} else if (x.getSituacao().equals("disponivel")) {
				totalDisponiveis++;
			}
		});
	}
	
	private BigDecimal faturamentoDoEvento(Evento evento) {
		totalFaturamento = BigDecimal.ZERO;
		evento.getListaDeVagas().forEach(x -> {
			totalFaturamento = totalFaturamento.add(new BigDecimal(x.getValor().toString()));
		});
		return totalFaturamento;
	}

	private void povoarLabels() {
		disponiveis = 0;
		vendidas = 0;
		reservadas = 0;
		if (selecioneUmEvento.getSelectionModel().getSelectedItem() == null) {
			vagasVendidas.setText(totalVendidas.toString());
			vagasReservadas.setText(totalReservadas.toString());
			vagasDisponiveis.setText(totalDisponiveis.toString());
			faturamento.setText(totalFaturamento.toString());
			data_evento.setText("-");
		} else {
			for (Evento x : eventos) {
				if (x.equals(selecioneUmEvento.getSelectionModel().getSelectedItem())) {
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
					data_evento.setText(x.getData() != null ? sdf.format(x.getData().getTime()) : "-");
					faturamento.setText(faturamentoDoEvento(x).toString());
				}
			}
		}
	}

	private void povoarGrafico() {
		grafico.setLegendVisible(false);
		grafico.getData().clear();
		Collections.sort(eventos);

		if (ordenarMaisVendidos.isSelected()) {
			Map<Evento, Integer> map = new LinkedHashMap<>();
			eventos.forEach(x -> {
				vendidas = 0;
				x.getListaDeVagas().forEach(y -> {
					vendidas = y.getSituacao().equals("vendida") ? vendidas + 1 : vendidas;
				});
				map.put(x, vendidas);
			});
			Map<Evento, Integer> mapOrdenado = map.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			mapOrdenado.forEach((x, y) -> {
				String formatoNome = x.getNome() + "\n(" + y + ")";
				XYChart.Series<String, Integer> dados = new XYChart.Series<>();
				dados.getData().add(new XYChart.Data<String, Integer>(formatoNome, y));
				grafico.getData().add(dados);
			});
		} else if (ordenarQuantidadeVagas.isSelected()) {
			eventos.forEach(x -> {
				XYChart.Series<String, Integer> dados = new XYChart.Series<>();
				dados.getData().add(new XYChart.Data<String, Integer>(x.getNome(), x.getListaDeVagas().size()));
				grafico.getData().add(dados);
			});
		} else if (ordenarMaisProximos.isSelected()) {
			Collections.sort(eventos, (p, s) -> {
				String primeiro = p.getData() != null ? sdf.format(p.getData().getTime()) : "01/01/2000";
				String segundo = p.getData() != null ? sdf.format(p.getData().getTime()) : "01/01/2000";
				return primeiro.compareToIgnoreCase(segundo);
			});
			
			eventos.forEach(x -> {
				String formatoNome = x.getData() != null ? x.getNome() + "\n" + sdf.format(x.getData().getTime())
					: x.getNome() + "\nSem data"; 
				XYChart.Series<String, Integer> dados = new XYChart.Series<>();
				dados.getData().add(new XYChart.Data<String, Integer>(formatoNome, 1));
				grafico.getData().add(dados);
			});
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		eventos = FXCollections.observableList(OperacoesDB.getEventos());
		eventos.forEach(x -> {
			vagas.addAll(x.getListaDeVagas());
		});
		selecioneUmEvento.setItems(eventos);
		ordenarMaisVendidos.setSelected(true);

		calcularTotais();

		povoarLabels();
		povoarGrafico();
	}
}
