package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuando las credenciales de inicio de sesion son invalidos
 */
public class InvalidCredentialException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Funcion que arroja el error
	 */
	public InvalidCredentialException() {
		super("Nombre o contraseña incorrectos");
	}
	
	

}
