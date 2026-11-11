package co.edu.unbosque.pokemagma.dto;

import java.util.Objects;
/**
 * DTO para transferir los datos de una fila del inventario
 */
public class InventoryItemDTO {
	/**
	 * Identificador de la fila de inventario
	 */
	private Long id;
	/**
	 * Identificador del jugador propietario
	 */
	private Long playerId;
	/**
	 * Codigo del sujeto
	 */
	private String itemCode;
	/*
	 * Cantidad del objeto
	 */
	private int quantity;
	
	/**
	 * Constructor vacio
	 */
	public InventoryItemDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor con todos los campos
	 * 
	 * @param id identificador de la fila
	 * @param playerId ID del jugador
	 * @param itemCode codigo del objeto
	 * @param quantity cantida disponible
	 */
	public InventoryItemDTO(Long id, Long playerId, String itemCode, int quantity) {
		super();
		this.id = id;
		this.playerId = playerId;
		this.itemCode = itemCode;
		this.quantity = quantity;
	}

	/**
	 * Getters y setters de los campos
	 * @return
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return codigo hash del DTO
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id, itemCode, playerId, Integer.valueOf(quantity));
	}

	/**
	 * Compara este DTO con otro por sus campos
	 * 
	 * @param obj objeto que se compara
	 * @return true si los campos coinciden
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryItemDTO other = (InventoryItemDTO) obj;
		return Objects.equals(id, other.id) && Objects.equals(itemCode, other.itemCode)
				&& Objects.equals(playerId, other.playerId) && quantity == other.quantity;
	}

	/**
	 * representacion en texto del DTO
	 */
	@Override
	public String toString() {
		return "InventoryItemDTO [id=" + id + ", playerId=" + playerId + ", itemCode=" + itemCode + ", quantity="
				+ quantity + "]";
	}
	
	

}
