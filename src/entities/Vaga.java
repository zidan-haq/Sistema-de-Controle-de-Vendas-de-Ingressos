package entities;

public class Vaga {
	Integer numero_vaga;
	String nome_evento;
	String situacao;
	Double valor;
	String detalhes = "";
	String numero_pessoa;

	public Vaga(Integer numero_vaga, String nome_evento, String situacao, Double valor, String detalhes, String numero_pessoa) {
		this.numero_vaga = numero_vaga;
		this.nome_evento = nome_evento;
		this.situacao = situacao;
		this.valor = valor;
		this.detalhes = detalhes;
		this.numero_pessoa = numero_pessoa;
	}

	public Integer getNumero_vaga() {
		return numero_vaga;
	}
	public void setNumero_vaga(Integer numero_vaga) {
		this.numero_vaga = numero_vaga;
	}
	public String getNome_evento() {
		return nome_evento;
	}
	public void setNome_evento(String nome_evento) {
		this.nome_evento = nome_evento;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public String getDetalhes() {
		return detalhes;
	}
	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}
	public void addDetalhes(String detalhes) {
		this.detalhes += "\n" + detalhes;
	}
	public String getNumero_pessoa() {
		return numero_pessoa;
	}
	public void setNumero_pessoa(String numero_pessoa) {
		this.numero_pessoa = numero_pessoa;
	}
	
	@Override
	public String toString() {
		return "Vaga número: " + numero_vaga.toString();
	}
}
