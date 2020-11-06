package entities;

import java.util.Calendar;

public class Pessoa {
	String nome;
	String numero;
	String email;
	Calendar nascimento;
	String historico = "";
	
	public Pessoa(String nome, String numero, String email, Calendar nascimento) {
		this.nome = nome;
		this.numero = numero;
		this.email = email;
		this.nascimento = nascimento;
	}
	
	public Pessoa(String nome, String numero, String email, Calendar nascimento, String historico) {
		this.nome = nome;
		this.numero = numero;
		this.email = email;
		this.nascimento = nascimento;
		this.historico = historico;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Calendar getNascimento() {
		return nascimento;
	}

	public void setNascimento(Calendar nascimento) {
		this.nascimento = nascimento;
	}

	public String getHistorico() {
		return historico;
	}

	public void addHistorico(String historico) {
		this.historico += "\n" + historico;
	}
	
	@Override
	public String toString() {
		return nome;
	}
}
