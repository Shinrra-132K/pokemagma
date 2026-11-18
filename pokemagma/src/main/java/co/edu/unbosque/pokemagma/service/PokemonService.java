package co.edu.unbosque.pokemagma.service;

import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.edu.unbosque.pokemagma.exception.ExternalServiceException;

/** Servicio de consulta de datos Pokémon en PokeAPI. */
@Service
public class PokemonService {

	/** URL base de la API Pokémon consultada por el servicio. */
	public static final String POKEAPI_URL = "https://pokeapi.co/api/v2";

	/** Estadísticas base de una especie obtenidas desde PokeAPI. */
	public record PokemonData(int id, String name, int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
	}

	/**
	 * Consulta y convierte las estadísticas de una especie Pokémon.
	 * 
	 * @param speciesId identificador de especie
	 * @return pokemon data
	 * @throws ExternalServiceException si la operación no puede completarse
	 */
	public PokemonData getPokemonData(int speciesId) throws ExternalServiceException {
		String response = getPokemon(String.valueOf(speciesId));
		JsonObject pokemon = JsonParser.parseString(response).getAsJsonObject();
		JsonArray stats = pokemon.getAsJsonArray("stats");

		int hp = 0;
		int attack = 0;
		int defense = 0;
		int speed = 0;

		for (JsonElement element : stats) {
			JsonObject statEntry = element.getAsJsonObject();
			String statName = statEntry.getAsJsonObject("stat").get("name").getAsString();
			int value = statEntry.get("base_stat").getAsInt();

			switch (statName) {
			case "hp" -> hp = value;
			case "attack" -> attack = value;
			case "defense" -> defense = value;
			case "speed" -> speed = value;
			default -> {
			}
			}
		}

		if (hp <= 0 || attack <= 0 || defense <= 0 || speed <= 0) {
			throw new ExternalServiceException();
		}

		return new PokemonData(pokemon.get("id").getAsInt(), pokemon.get("name").getAsString(), hp, attack, defense,
				speed);
	}

	/**
	 * Consulta un Pokémon por nombre o identificador.
	 * 
	 * @param nameOrId name or id
	 * @return pokemon
	 * @throws ExternalServiceException si la operación no puede completarse
	 */
	public String getPokemon(String nameOrId) throws ExternalServiceException {
		String url = POKEAPI_URL + "/pokemon/" + nameOrId.toLowerCase() + "/";
		return ExternalHTTPRequestHandler.doGetAndParse(url);
	}

	/**
	 * Consulta la información de especie en PokeAPI.
	 * 
	 * @param nameOrId name or id
	 * @return pokemon species
	 * @throws ExternalServiceException si la operación no puede completarse
	 */
	public String getPokemonSpecies(String nameOrId) throws ExternalServiceException {
		String url = POKEAPI_URL + "/pokemon-species/" + nameOrId.toLowerCase() + "/";
		return ExternalHTTPRequestHandler.doGetAndParse(url);
	}

	/**
	 * Consulta una página de resultados de PokeAPI.
	 * 
	 * @param limit  limit
	 * @param offset offset
	 * @return pokemon list
	 * @throws ExternalServiceException si la operación no puede completarse
	 */
	public String getPokemonList(int limit, int offset) throws ExternalServiceException {
		if (limit <= 0 || offset < 0) {
			throw new ExternalServiceException();
		}
		String url = POKEAPI_URL + "/pokemon?limit=" + limit + "&offset=" + offset;
		return ExternalHTTPRequestHandler.doGetAndParse(url);
	}
}
