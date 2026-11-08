package co.edu.unbosque.pokemagma.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "game_event")
public class GameEvent {
	
	public enum EventType {
		LOGIN, LOGOUT, POKEMON_CAUGHT, BATTLE
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "player_id")
	private Long playerId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EventType type;
	
	@Column(nullable = false)
	private String description;
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	
	public GameEvent() {
		// TODO Auto-generated constructor stub
	}


	public GameEvent(Long playerId, EventType type, String description) {
		super();
		this.playerId = playerId;
		this.type = type;
		this.description = description;
		this.createdAt = Instant.now();
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


	public EventType getType() {
		return type;
	}


	public void setType(EventType type) {
		this.type = type;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Instant getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}


	@Override
	public String toString() {
		return "GameEvent [id=" + id + ", playerId=" + playerId + ", type=" + type + ", description=" + description
				+ ", createdAt=" + createdAt + "]";
	}
	
	
	
	
	
	
	

}
