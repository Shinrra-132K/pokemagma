package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuando un correo electronico ya se encuentra en uso
 */
public class EmailAlreadyRegisteredException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Describe el error y la razon del error
	 */
	public EmailAlreadyRegisteredException() {
		super("El correo electronico ingresado ya esta en uso, ingrese uno diferente");
	}

}
