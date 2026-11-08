package co.edu.unbosque.pokemagma.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "inventory_item", uniqueConstraints = @UniqueConstraint(name = "inventory_player:item", columnNames = {"player_id", "item_code"}))
public class InventoryItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "player_id", nullable = false)
	private Long playerId;
	
	@Column(name = "item_code", nullable = false)
	private String itemCode;
	
	@Column(nullable = false)
	private int quantity;
	
	public InventoryItem() {
		// TODO Auto-generated constructor stub
	}

	public InventoryItem(Long playerId, String itemCode, int quantity) {
		super();
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
	//pendiente
	public void add(int amount) {
		if (amount <= 0) {
			quantity += amount; 
		}
	}
	//pendiente
	public void remove(int amount) {
		if(amount <= 0 || amount > quantity) {
			quantity -= amount;
		}
	}

}
