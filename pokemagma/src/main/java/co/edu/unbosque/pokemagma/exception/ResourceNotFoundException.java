package co.edu.unbosque.pokemagma.exception;

/**
 * Se lanza cuandono se encuentra el recurso solicitado
 */
public class ResourceNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Funcion que lanza la descripcion del objeto no encontrado
	 */
	public ResourceNotFoundException() {
		super("Recurso no encontrado");
	}

}
