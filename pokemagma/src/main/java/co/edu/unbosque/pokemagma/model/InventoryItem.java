package co.edu.unbosque.pokemagma.model;

import java.util.Objects;

import co.edu.unbosque.pokemagma.exception.QuantityNotValidException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Representa una clase de objeto y su cantidad en el inventario de un jugador.
 */
@Entity
@Table(name = "inventory_item", uniqueConstraints = @UniqueConstraint(name = "inventory_player:item", columnNames = {
		"player_id", "item_code" }))
public class InventoryItem {

	/**
	 * Identificador unico de esta fila del inventario
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Id del jugador dueño del objeto
	 */
	@Column(name = "player_id", nullable = false)
	private Long playerId;

	/**
	 * Codigo del objeto, como una pokeball o una pocion
	 */
	@Column(name = "item_code", nullable = false)
	private String itemCode;

	/**
	 * Unidades de este objeto que tiene el jugador
	 */
	@Column(nullable = false)
	private int quantity;

	/**
	 * Constructor vacio
	 */
	public InventoryItem() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea una fila de inventario
	 * 
	 * @param playerId ID del jugador
	 * @param itemCode codigo del objeto
	 * @param quantity cantidad inicial
	 */
	public InventoryItem(Long playerId, String itemCode, int quantity) {
		super();
		this.playerId = playerId;
		this.itemCode = itemCode;
		this.quantity = quantity;
	}

	/**
	 * Obtiene el identificador de la fila del inventario.
	 *
	 * @return ID de la fila
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Establece el identificador de la fila del inventario.
	 *
	 * @param id identificador de la fila
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Obtiene el identificador del jugador propietario.
	 *
	 * @return ID del jugador
	 */
	public Long getPlayerId() {
		return playerId;
	}

	/**
	 * Establece el identificador del jugador propietario.
	 *
	 * @param playerId identificador del jugador
	 */
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	/**
	 * Obtiene el código del objeto.
	 *
	 * @return código del objeto
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * Establece el código del objeto.
	 *
	 * @param itemCode código del objeto
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	/**
	 * Obtiene la cantidad disponible del objeto.
	 *
	 * @return cantidad de unidades
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Establece la cantidad disponible del objeto.
	 *
	 * @param quantity cantidad de unidades
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Añade unidades al inventario.
	 *
	 * @param amount cantidad que se añadirá
	 * @throws QuantityNotValidException si la cantidad no es positiva
	 */
	public void add(int amount) throws QuantityNotValidException {
	    if (amount <= 0) {
	        throw new QuantityNotValidException();
	    }

	    quantity += amount;
	}

	/**
	 * Retira unidades del inventario.
	 *
	 * @param amount cantidad que se retirará
	 * @throws QuantityNotValidException si la cantidad no es positiva
	 *         o supera la cantidad disponible
	 */
	public void remove(int amount) throws QuantityNotValidException {
	    if (amount <= 0 || amount > quantity) {
	        throw new QuantityNotValidException();
	    }

	    quantity -= amount;
	}

	/**
	 * Devuelve un hash durante la signacion del ID
	 * 
	 * @return codigo hash de la clase
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, itemCode, playerId, Integer.valueOf(quantity));
	}

	/**
	 * Compara filas de inventario por su ID persistido
	 * 
	 * @param obj objeto que se compara
	 * @return true si ambos tienen el mismo ID
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryItem other = (InventoryItem) obj;
		return Objects.equals(id, other.id) && Objects.equals(itemCode, other.itemCode)
				&& Objects.equals(playerId, other.playerId) && quantity == other.quantity;
	}

	/**
	 * Devuelve una representacion textual
	 * 
	 * @return texto con los datos del objeto
	 */
	@Override
	public String toString() {
		return "InventoryItem [id=" + id + ", playerId=" + playerId + ", itemCode=" + itemCode + ", quantity="
				+ quantity + "]";
	}

}
