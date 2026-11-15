package co.edu.unbosque.pokemagma.exception;
/**
 * Se lanza cuando la cantidad del objeto no es valida
 */
public class QuantityNotValidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 *Funcion que arroja el error 
	 */
	public QuantityNotValidException() {
		super("Cantidad no valida");
	}
	
	

}
