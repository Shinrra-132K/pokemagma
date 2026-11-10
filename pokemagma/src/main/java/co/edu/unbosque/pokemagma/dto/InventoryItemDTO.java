package co.edu.unbosque.pokemagma.dto;

import java.util.Objects;

public class InventoryItemDTO {
	
	private Long id;
	private Long playerId;
	private String itemCode;
	private int quantity;
	
	public InventoryItemDTO() {
		// TODO Auto-generated constructor stub
	}

	public InventoryItemDTO(Long id, Long playerId, String itemCode, int quantity) {
		super();
		this.id = id;
		this.playerId = playerId;
		this.itemCode = itemCode;
		this.quantity = quantity;
	}

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

	@Override
	public int hashCode() {
		return Objects.hash(id, itemCode, playerId, Integer.valueOf(quantity));
	}

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

	@Override
	public String toString() {
		return "InventoryItemDTO [id=" + id + ", playerId=" + playerId + ", itemCode=" + itemCode + ", quantity="
				+ quantity + "]";
	}
	
	

}
