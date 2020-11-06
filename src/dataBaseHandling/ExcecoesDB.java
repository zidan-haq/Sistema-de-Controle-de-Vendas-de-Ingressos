package dataBaseHandling;

public class ExcecoesDB extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ExcecoesDB(String msg) {
		super(msg);
	}
}
