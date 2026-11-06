package co.edu.unbosque.pokemagma.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokemon")
public class Pokemon {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int pokedexNumber;
	private String name;
	private int generation;
	private String tyoeOne;
	private String tyoeTwo;
	private String imageUrl;
	
	public Pokemon() {
		// TODO Auto-generated constructor stub
	}

	public Pokemon(Long id, int pokedexNumber, String name, int generation, String tyoeOne, String tyoeTwo,
			String imageUrl) {
		super();
		this.id = id;
		this.pokedexNumber = pokedexNumber;
		this.name = name;
		this.generation = generation;
		this.tyoeOne = tyoeOne;
		this.tyoeTwo = tyoeTwo;
		this.imageUrl = imageUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getPokedexNumber() {
		return pokedexNumber;
	}

	public void setPokedexNumber(int pokedexNumber) {
		this.pokedexNumber = pokedexNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public String getTyoeOne() {
		return tyoeOne;
	}

	public void setTyoeOne(String tyoeOne) {
		this.tyoeOne = tyoeOne;
	}

	public String getTyoeTwo() {
		return tyoeTwo;
	}

	public void setTyoeTwo(String tyoeTwo) {
		this.tyoeTwo = tyoeTwo;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	
	

}
