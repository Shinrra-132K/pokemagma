package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuando el genero no es seleccionado
 */
public class GenderNotValidException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Funcion que lanza el error y explica el parametro
	 */
	public GenderNotValidException() {
		super("Debe seleccionar un genero");
	}

}
