package viewControllers.observers;

import javafx.scene.control.Label;


public class Observador {
	public static Object objeto; // Objetos transferidos entre as classes

	/*
	 * As classes utilizam essa variável para sinalizar a outra classe a necessidade de se realizar algum procedimento.
	 * quando uma classe modifica esse valor para true, outra classe deve imediatamente realizar uma ação e mudá-lo para false novamente.
	 */
	public static boolean fazerAcao = false;

	public static Label nomeDoEventoAtual; // esse é o label da coluna controle de ingressos da tela principal
	private static boolean jaAberto = false;
		
	// Esse método é utilizado para manter a referência na memória ao primeiro Label eventoAtual criado pela tela principal
	public static void setNomeDoEventoSelecionado(Label eventoAtual) {
		if(!jaAberto) {
			nomeDoEventoAtual = eventoAtual;
		}
		jaAberto = true;
	}
}
