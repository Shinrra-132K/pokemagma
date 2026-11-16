package co.edu.unbosque.pokemagma.exception;

/**
 * Selanza cuando se intenta axceder sin que se verifique el correo electronico
 */
public class EmailNotVerifiedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Funcion que lanza la descripcion del error
	 */
	public EmailNotVerifiedException() {
		super("No puedes continuar si no verificas tu correo electronico.");
	}
	
	

}
