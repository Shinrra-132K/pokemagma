package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuando las credenciales de inicio de sesion son invalidos
 */
public class InvalidCredentialException extends Exception {

	/**
	 * Identificador de serialización de la excepción
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Funcion que arroja el error
	 */
	public InvalidCredentialException() {
		super("Nombre o contraseña incorrectos");
	}

}
