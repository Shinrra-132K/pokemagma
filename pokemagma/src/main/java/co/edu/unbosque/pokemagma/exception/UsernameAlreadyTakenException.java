package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuando el nombre de usario ya se encuentra registrado
 */
public class UsernameAlreadyTakenException extends Exception {

	/**
	 * Identificador de serialización de la excepción
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Funcion que arroja la descripcion del error sin decirle al ususario que el
	 * nombre de ususario ya existe
	 */
	public UsernameAlreadyTakenException() {
		super("Por favor elija otro nombre de ususario");
	}

}
