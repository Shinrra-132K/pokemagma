package co.edu.unbosque.pokemagma.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "player_pokemon")
public class PlayerPokemon {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "player_id")
	private Player player;
	
	@ManyToOne
	@JoinColumn(name = "pokemon_id")
	private Pokemon pokemon;
	
	private int level;
	private int experience;
	private int currentHp;
	private int maxHp;
	private boolean starter;
	private LocalDateTime capturedAt;
	
	public PlayerPokemon() {
		// TODO Auto-generated constructor stub
	}

	public PlayerPokemon(Long id, Player player, Pokemon pokemon, int level, int experience, int currentHp, int maxHp,
			boolean starter, LocalDateTime capturedAt) {
		super();
		this.id = id;
		this.player = player;
		this.pokemon = pokemon;
		this.level = level;
		this.experience = experience;
		this.currentHp = currentHp;
		this.maxHp = maxHp;
		this.starter = starter;
		this.capturedAt = capturedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Pokemon getPokemon() {
		return pokemon;
	}

	public void setPokemon(Pokemon pokemon) {
		this.pokemon = pokemon;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public int getCurrentHp() {
		return currentHp;
	}

	public void setCurrentHp(int currentHp) {
		this.currentHp = currentHp;
	}

	public int getMaxHp() {
		return maxHp;
	}

	public void setMaxHp(int maxHp) {
		this.maxHp = maxHp;
	}

	public boolean isStarter() {
		return starter;
	}

	public void setStarter(boolean starter) {
		this.starter = starter;
	}

	public LocalDateTime getCapturedAt() {
		return capturedAt;
	}

	public void setCapturedAt(LocalDateTime capturedAt) {
		this.capturedAt = capturedAt;
	}
	
	
	

}
