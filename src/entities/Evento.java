package entities;

import java.util.Calendar;
import java.util.List;

public class Evento implements Comparable<Evento> {
	String nome;
	Integer quant_vagas;
	Double valor_vaga;
	Calendar data;
	String detalhes;
	List<Vaga> listaDeVagas;

	public Evento(String nome, Integer quant_vagas, Double valor_vaga, Calendar data, String detalhes,
			List<Vaga> listaDeVagas) {
		this.nome = nome;
		this.quant_vagas = quant_vagas;
		this.valor_vaga = valor_vaga;
		this.data = data;
		this.detalhes = detalhes;
		this.listaDeVagas = listaDeVagas;
	}

	public Evento(String nome, Integer quant_vagas, Double valor_vaga, Calendar data, String detalhes) {
		this.nome = nome;
		this.quant_vagas = quant_vagas;
		this.valor_vaga = valor_vaga;
		this.data = data;
		this.detalhes = detalhes;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getQuant_vagas() {
		return quant_vagas;
	}

	public void setQuant_vagas(Integer quant_vagas) {
		this.quant_vagas = quant_vagas;
	}

	public Double getValor_vaga() {
		return valor_vaga;
	}

	public void setValor_vaga(Double valor_vaga) {
		this.valor_vaga = valor_vaga;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public String getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}

	public List<Vaga> getListaDeVagas() {
		return listaDeVagas;
	}

	public void setListaDeVagas(List<Vaga> listaDeVagas) {
		this.listaDeVagas = listaDeVagas;
	}

	@Override
	public String toString() {
		return nome;
	}

	@Override
	public int compareTo(Evento evento) {
		if (this.quant_vagas < evento.quant_vagas) {
			return -1;
		} else if (this.quant_vagas > evento.quant_vagas) {
			return 1;
		}
		return this.nome.compareToIgnoreCase(evento.getNome());
	}
}
