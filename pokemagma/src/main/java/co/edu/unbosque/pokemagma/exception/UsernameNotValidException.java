package co.edu.unbosque.pokemagma.exception;

/**
 * Indica que el nombre de ususario no cumple con las reglas definidas
 */
public class UsernameNotValidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Funcion que hace descripcion al error y explica los parametros
	 */
	public UsernameNotValidException() {
		super("El nombre de usuario no sigue el estandar adecuado\n"
				+ "minimo 15 caracteres,\n"
				+ "maximo 15 caracteres,\n"
				+ "debe contener minimo un numero y un simbolo.");
	}

}
