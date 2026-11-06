package co.edu.unbosque.pokemagma.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "player")
public class Player {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	private int money;
	private int posicionX;
	private int posicionY;
	private String currentMap;
	
	@ManyToOne
	@JoinColumn(name = "started_option_generation1_id")
	private Pokemon startedOptionGeneration1;
	
	@ManyToOne
	@JoinColumn(name = "started_option_generation2_id")
	private Pokemon startedOptionGeneration2;

	@ManyToOne
	@JoinColumn(name = "started_option_generation3_id")
	private Pokemon startedOptionGeneration3;
	
	@ManyToOne
	@JoinColumn(name = "selected_starter_id")
	private Pokemon selectedStarter;
	
	@OneToMany(mappedBy = "player")
	private List<PlayerPokemon> pokemons;
	
	
	public Player() {
		// TODO Auto-generated constructor stub
	}


	public Player(Long id, User user, int money, int posicionX, int posicionY, String currentMap,
			Pokemon startedOptionGeneration1, Pokemon startedOptionGeneration2, Pokemon startedOptionGeneration3,
			Pokemon selectedStarter, List<PlayerPokemon> pokemons) {
		super();
		this.id = id;
		this.user = user;
		this.money = money;
		this.posicionX = posicionX;
		this.posicionY = posicionY;
		this.currentMap = currentMap;
		this.startedOptionGeneration1 = startedOptionGeneration1;
		this.startedOptionGeneration2 = startedOptionGeneration2;
		this.startedOptionGeneration3 = startedOptionGeneration3;
		this.selectedStarter = selectedStarter;
		this.pokemons = pokemons;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public int getMoney() {
		return money;
	}


	public void setMoney(int money) {
		this.money = money;
	}


	public int getPosicionX() {
		return posicionX;
	}


	public void setPosicionX(int posicionX) {
		this.posicionX = posicionX;
	}


	public int getPosicionY() {
		return posicionY;
	}


	public void setPosicionY(int posicionY) {
		this.posicionY = posicionY;
	}


	public String getCurrentMap() {
		return currentMap;
	}


	public void setCurrentMap(String currentMap) {
		this.currentMap = currentMap;
	}


	public Pokemon getStartedOptionGeneration1() {
		return startedOptionGeneration1;
	}


	public void setStartedOptionGeneration1(Pokemon startedOptionGeneration1) {
		this.startedOptionGeneration1 = startedOptionGeneration1;
	}


	public Pokemon getStartedOptionGeneration2() {
		return startedOptionGeneration2;
	}


	public void setStartedOptionGeneration2(Pokemon startedOptionGeneration2) {
		this.startedOptionGeneration2 = startedOptionGeneration2;
	}


	public Pokemon getStartedOptionGeneration3() {
		return startedOptionGeneration3;
	}


	public void setStartedOptionGeneration3(Pokemon startedOptionGeneration3) {
		this.startedOptionGeneration3 = startedOptionGeneration3;
	}


	public Pokemon getSelectedStarter() {
		return selectedStarter;
	}


	public void setSelectedStarter(Pokemon selectedStarter) {
		this.selectedStarter = selectedStarter;
	}


	public List<PlayerPokemon> getPokemons() {
		return pokemons;
	}


	public void setPokemons(List<PlayerPokemon> pokemons) {
		this.pokemons = pokemons;
	}
	
	
	
	

}
