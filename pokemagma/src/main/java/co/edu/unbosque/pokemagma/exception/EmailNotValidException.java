package co.edu.unbosque.pokemagma.exception;
/**
 * Indica que el correo electronico no es valido
 */
public class EmailNotValidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Funcion que lanza el error y explica los parametros
	 */
	public EmailNotValidException() {
		super("Ingresa una dirección de correo electrónico válida (ej. nombre@ejemplo.com).");
	}

}
