package viewControllers.constraints;

import java.text.ParseException;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javax.swing.text.MaskFormatter;

import exceptionHandling.TelaDeExcecoes;

public class MascaraTextField {
	    private final MaskFormatter mf;
	    private TextField tf;
	    private String CaracteresValidos;
	    private String mask;

	    public MascaraTextField(TextField tf, String caracteresValidos) {
	    	mf = new MaskFormatter();
	        this.tf = tf;
	        this.CaracteresValidos = caracteresValidos;
	    }

	    public void formatter(TextField tf, String CaracteresValidos, String mask) {
	        try {
	            mf.setMask(mask);
	            mf.setPlaceholderCharacter('_');
	            
	        } catch (ParseException ex) {
	            TelaDeExcecoes.lancarExcecao(AlertType.ERROR, "Erro", "Não foi possível converter o número.",
	            		"houve um erro inesperado ao converter o número.");
	        }

	        mf.setValidCharacters(CaracteresValidos);
	        mf.setValueContainsLiteralCharacters(false);
	        String text = tf.getText().replaceAll("[\\W]", "");
	        
	        boolean repetir = true;
	        while (repetir) {

	            char ultimoCaractere;

	            if (text.equals("")) {
	                break;
	            } else {
	                ultimoCaractere = text.charAt(text.length() - 1);
	            }

	            try {
	                text = mf.valueToString(text);
	                repetir = false;
	            } catch (ParseException ex) {
	                text = text.replace(ultimoCaractere + "", "");
	                repetir = true;
	            }

	        }

	        tf.setText(text);

	        if (!text.equals("")) {
	            for (int i = 0; tf.getText().charAt(i) != '_'; i++) {
	                tf.forward();
	                if(i == tf.getLength() - 1) {
	                	tf.positionCaret(tf.getLength());
	                	break;
	                }
	            }
	        }
	    }

	    public void formatter(){
	        formatter(tf, CaracteresValidos, mask);
	    }

	    public TextField getTf() {
	        return tf;
	    }

	    public void setTf(TextField tf) {
	        this.tf = tf;
	    }

	    public String getCaracteresValidos() {
	        return CaracteresValidos;
	    }

	    public void setCaracteresValidos(String CaracteresValidos) {
	        this.CaracteresValidos = CaracteresValidos;
	    }
	    
	    public String getMask() {
	        return mask;
	    }

	    public void setMask(String mask) {
	        this.mask = mask;
	    }
}