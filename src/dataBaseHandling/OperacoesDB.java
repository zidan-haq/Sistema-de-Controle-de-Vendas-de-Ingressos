package dataBaseHandling;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import entities.Evento;
import entities.Pessoa;
import entities.Vaga;
import exceptionHandling.TelaDeExcecoes;
import javafx.scene.control.Alert.AlertType;

public class OperacoesDB {
	public static List<Evento> getEventos() {
		List<Evento> lista = new ArrayList<>();
		try (ResultSet rs = ConexaoDB.conectar().createStatement().executeQuery("select * from eventos;")) {
			while (rs.next()) {
				String nome = rs.getString("nome");
				Integer quant_vagas = rs.getInt("quant_vagas");
				Double valor_vaga = rs.getDouble("valor_vaga");
				Calendar data = Calendar.getInstance();
				if(rs.getDate("data") != null) {
					data.setTime(rs.getDate("data"));
				} else {
					data = null;
				}
				String detalhes = rs.getString("detalhes");

				lista.add(new Evento(nome, quant_vagas, valor_vaga, data, detalhes, getVagasPorNomeEvento(nome)));
			}
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
		return lista;
	}

	public static void excluirEventoPorNome(String nome) {
		excluirVagasDeEvento(nome);
		String sql = "delete from eventos where nome = ?;";

		try (PreparedStatement ps = ConexaoDB.conectar().prepareStatement(sql)) {
			ps.setString(1, nome);
			ps.executeUpdate();
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
	}

	public static void adicionarEvento(Evento evento) {
		String sql = "insert into eventos values(?, ?, ?, ?, ?);"; // nome, quant_vagas, valor_vaga, data,
																	// detalhes
		try (PreparedStatement ps = ConexaoDB.conectar().prepareStatement(sql)) {
			ps.setString(1, evento.getNome());
			ps.setInt(2, evento.getQuant_vagas());
			ps.setDouble(3, evento.getValor_vaga());
			ps.setDate(4, evento.getData() == null ? null : new java.sql.Date(evento.getData().getTimeInMillis()));
			ps.setString(5, evento.getDetalhes());

			ps.executeQuery();
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
		adicionarVagas(evento.getListaDeVagas());
	}

	public static void atualizarEvento(Evento novoEvento) {
		String sql = "update eventos set quant_vagas = ?, valor_vaga = ?, data = ?, detalhes = ? "
				+ "where nome = ?;"; // quant_vagas, valor_vaga, data, detalhes, nome_evento
		try (PreparedStatement ps = ConexaoDB.conectar().prepareStatement(sql)) {
			ps.setInt(1, novoEvento.getQuant_vagas());
			ps.setDouble(2, novoEvento.getValor_vaga());
			ps.setDate(3, novoEvento.getData() == null ? null : new java.sql.Date(novoEvento.getData().getTimeInMillis()));
			ps.setString(4, novoEvento.getDetalhes());
			ps.setString(5, novoEvento.getNome());
			
			ps.executeQuery();
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
		excluirVagasDeEvento(novoEvento.getNome());
		adicionarVagas(novoEvento.getListaDeVagas());
	}

	public static List<Pessoa> getPessoas() {
		List<Pessoa> lista = new ArrayList<>();
		try (ResultSet rs = ConexaoDB.conectar().createStatement().executeQuery("select * from pessoas;")) {
			while (rs.next()) {
				String nome = rs.getString("nome");
				String numero = rs.getString("numero");
				String email = rs.getString("email");
				Calendar nascimento = Calendar.getInstance();
				if(rs.getDate("nascimento") != null) {
					nascimento.setTime(rs.getDate("nascimento"));
				} else {
					nascimento = null;
				}
				String historico = rs.getString("historico");

				lista.add(new Pessoa(nome, numero, email, nascimento, historico));
			}
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
		return lista;
	}
	
	public static void excluirPessoaPorNumero(String numero) {
		excluirVinculacaoVagaPessoa(numero);
		String sql = "delete from pessoas where numero = ?;";

		try (PreparedStatement ps = ConexaoDB.conectar().prepareStatement(sql)) {
			ps.setString(1, numero);
			ps.executeUpdate();
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
	}
	
	public static void adicionarPessoa(Pessoa pessoa) {
		String sql = "insert into pessoas values(?, ?, ?, ?, ?);"; // numero, nome, email, nascimento, historico

		try (PreparedStatement ps = ConexaoDB.conectar().prepareStatement(sql)) {
			ps.setString(1, pessoa.getNumero());
			ps.setString(2, pessoa.getNome());
			ps.setString(3, pessoa.getEmail());
			ps.setDate(4, pessoa.getNascimento() == null ? null : new java.sql.Date(pessoa.getNascimento().getTimeInMillis()));
			ps.setString(5, pessoa.getHistorico());

			ps.executeQuery();
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
	}
	
	public static void atualizarPessoa(Pessoa pessoa) {
		String sql = "update pessoas set nome = ?, email = ?, nascimento = ?, historico = ? "
				+ "where numero = ?;"; // nome, email, nascimento, historico, numero
		try (PreparedStatement ps = ConexaoDB.conectar().prepareStatement(sql)) {
			ps.setString(1, pessoa.getNome());
			ps.setString(2, pessoa.getEmail());
			ps.setDate(3, pessoa.getNascimento() == null ? null : new java.sql.Date(pessoa.getNascimento().getTimeInMillis()));
			ps.setString(4, pessoa.getHistorico());
			ps.setString(5, pessoa.getNumero());
			
			ps.executeQuery();
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
	}

	public static List<Vaga> getVagasPorNomeEvento(String nomeEvento) {
		List<Vaga> lista = new ArrayList<>();
		String sql = "select * from vagas where nome_evento = '" + nomeEvento + "';";
		
		try (ResultSet rs = ConexaoDB.conectar().createStatement().executeQuery(sql)) {
			while (rs.next()) {
				Integer numero_vaga = rs.getInt("numero_vaga");
				String nome_evento = rs.getString("nome_evento");
				String situacao = rs.getString("situacao");
				Double valor = rs.getDouble("valor");
				String detalhes = rs.getString("detalhes");
				String numero_pessoa = rs.getString("numero_pessoa");
				
				lista.add(new Vaga(numero_vaga, nome_evento, situacao, valor, detalhes, numero_pessoa));
			}
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
		return lista;
	}
	
	public static void adicionarVagas(List<Vaga> lista_vagas) {
		for (Vaga x : lista_vagas) {
			String sql = "insert into vagas values (?, ?, ?, ?, ?, ?);"; 
			// numero_vaga, nome_evento, situacao, valor, detalhes, numero_pessoa
					
			try (PreparedStatement ps = ConexaoDB.conectar().prepareStatement(sql)) {
				ps.setInt(1, x.getNumero_vaga());
				ps.setString(2, x.getNome_evento());
				ps.setString(3, x.getSituacao());
				ps.setDouble(4, x.getValor());
				ps.setString(5, x.getDetalhes());
				ps.setString(6, x.getNumero_pessoa());
							
				ps.executeQuery();
			} catch (SQLException e) {
				TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
			}
		}
	}
	
	public static void atualizarVaga(Vaga vaga) {
		String sql = "update vagas set situacao = ?, valor = ?, detalhes = ?, numero_pessoa = ? "
				+ "where numero_vaga = ? and nome_evento = ?;"; // situacao, valor, detalhes, numero_pessoa, numero_vaga, nome_evento
				
		try (PreparedStatement ps = ConexaoDB.conectar().prepareStatement(sql)) {
			ps.setString(1, vaga.getSituacao());
			ps.setDouble(2, vaga.getValor());
			ps.setString(3, vaga.getDetalhes());			
			if(vaga.getNumero_pessoa() == null) {
				ps.setNull(4, Types.INTEGER);
			} else {
				ps.setString(4, vaga.getNumero_pessoa());
			}
			ps.setInt(5, vaga.getNumero_vaga());
			ps.setString(6, vaga.getNome_evento());
						
			ps.executeQuery();
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
	}

	private static void excluirVagasDeEvento(String nome_evento) {
		String sql = "delete from vagas where nome_evento = ?;";
		try (PreparedStatement ps = ConexaoDB.conectar().prepareStatement(sql)) {
			ps.setString(1, nome_evento);
			ps.executeUpdate();
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
	}
	
	private static void excluirVinculacaoVagaPessoa(String numero) {
		String sql = "update vagas set numero_pessoa = null where numero_pessoa = " + numero + ";";
		try (ResultSet rs = ConexaoDB.conectar().createStatement().executeQuery(sql)) {
		} catch (SQLException e) {
			TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "SQLException", e.getMessage());
		}
	}
}
