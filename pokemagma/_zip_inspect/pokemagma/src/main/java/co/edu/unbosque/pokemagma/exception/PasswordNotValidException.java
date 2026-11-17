package co.edu.unbosque.pokemagma.exception;

/**
 * Indica que la contraseña no cumple con los parametros establecidos
 */
public class PasswordNotValidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Funcion que lanza la descripcion del error y explica los parametros
	 */
	public PasswordNotValidException() {
		super("La contraseña no cumple con los requisitos: debe tener al menos 8 caracteres e incluir mayúsculas, minúsculas, números y símbolos.");
	}

}
