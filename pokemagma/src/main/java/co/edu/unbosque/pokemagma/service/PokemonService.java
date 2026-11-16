package co.edu.unbosque.pokemagma.service;

import org.springframework.stereotype.Service;

/**
 * Servicio para consultar informacion pokemon en PokeApi
 * 
 * Los datos se obtienen desde la Api externa y no se guardan como entidades propias en la base de datos
 */
@Service
public class PokemonService {
	
	/**
	 * Direccion base de la Api de pokemon
	 */
	public static final String POKEAPI_URL = "https://pokeapi.co/api/v2";
	
	/**
	 * Constructor vacio del servicio
	 */
	public PokemonService() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Consulta un pokemon por nombre o identificador
	 * 
	 * @param nameOrId nombre o ID del pokemon
	 * @return respuesta JSOn de PokeApi formateada
	 */
	public String getPokemon(String nameOrId) {
		String url = POKEAPI_URL + "/pokemon/" + nameOrId.toLowerCase() + "/";
		return ExternalHTTPRequestHandler.doGetAndParse(url);
	}
	
	/**
	 * Consulta la informacion de expecie por nombre o identificador
	 * 
	 * @param nameOrId nombre o ID de la especie
	 * @return respuesta JSON formateada
	 */
	public String getPokemonSpecies(String nameOrId) {
		String url = POKEAPI_URL + "/pokemon-species/" + nameOrId.toLowerCase() + "/";
		return ExternalHTTPRequestHandler.doGetAndParse(url);
	}
	
	/**
	 * Consulta una pagina de resultados de pokemon
	 * 
	 * @param limit maximo de resultados
	 * @param offset posiciones desde la que se obtienen los resultados
	 * @return respuesta JSON de PokeApi formateada
	 */
	public String getPokemonList(int limit, int offset) {
		String url = POKEAPI_URL + "pokemon?limit=" + limit + "&offset=" + offset;
		return ExternalHTTPRequestHandler.doGetAndParse(url);
	}
	

}
