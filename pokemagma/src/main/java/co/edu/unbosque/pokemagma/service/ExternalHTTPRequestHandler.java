package co.edu.unbosque.pokemagma.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.Strictness;

/**
 * Ejecuta solicitudes HTTP a servicios externos y formatea respuestas JSON
 */
public class ExternalHTTPRequestHandler {

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(10)).build();

	/**
	 * Constructor vacio
	 */
	public ExternalHTTPRequestHandler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Ejecuta una solicitud GET y devuelve la respuesta JSON formateada
	 * 
	 * @param url direccion a consultar
	 * @return cuerpo JSON formateado, o null si este falla
	 */
	public static String doGetAndParse(String url) {
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
				.header("Content-Type", "application/json").build();
		
		HttpResponse<String> response = null;
		try {
			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (response == null) {
			return null;
		}
		
		System.out.println("Status code -> " + response.statusCode());
		return prettyPrintUsingGson(response.body());

	}
	
	/**
	 * Convierte una cadena JSON a formato legible
	 * 
	 * @param uglyJson JSON recibido sin formato
	 * @return JSON con formato e identacion
	 */
	public static String prettyPrintUsingGson(String uglyJson) {
		Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().create();
		JsonElement jsonElement = JsonParser.parseString(uglyJson);
		String prettyJsonString = gson.toJson(jsonElement);
		return prettyJsonString;
	}

}
