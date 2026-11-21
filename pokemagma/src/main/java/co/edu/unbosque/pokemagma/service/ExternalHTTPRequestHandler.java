package co.edu.unbosque.pokemagma.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.Strictness;

import co.edu.unbosque.pokemagma.exception.ExternalServiceException;

/**
 * Agrupa operaciones HTTP GET usadas para consultar servicios externos que
 * devuelven respuestas JSON.
 *
 * Ofrece una operación que valida la respuesta y la procesa como JSON, y otra
 * que devuelve directamente el cuerpo recibido
 */
public class ExternalHTTPRequestHandler {

	/**
	 * Cliente HTTP reutilizable para las consultas a servicios externos.
	 */
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
			.build();

	/**
	 * Constructor vacio
	 */
	public ExternalHTTPRequestHandler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Envía una solicitud GET y devuelve el cuerpo de la respuesta.
	 *
	 * @param url dirección del servicio externo
	 * @return cuerpo de la respuesta cuando el estado HTTP es exitoso
	 * @throws IOException          si ocurre un error de conexión o el servicio
	 *                              devuelve un estado HTTP fuera del rango 200–299
	 * @throws InterruptedException si se interrumpe la solicitud
	 */
	public static String doGetAndParse(String url) throws ExternalServiceException {
		try {
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(15))
					.header("Accept", "application/json").GET().build();

			HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() < 200 || response.statusCode() >= 300) {
				throw new ExternalServiceException();
			}

			return prettyPrintUsingGson(response.body());
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new ExternalServiceException();
		} catch (IOException | IllegalArgumentException exception) {
			throw new ExternalServiceException();
		} catch (RuntimeException exception) {
			throw new ExternalServiceException();
		}
	}

	/**
	 * Formatea una cadena JSON para facilitar su lectura.
	 * 
	 * @param json cadena JSON
	 * @return respuesta generada
	 */
	public static String prettyPrintUsingGson(String json) {
		Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT).setPrettyPrinting().create();
		JsonElement element = JsonParser.parseString(json);
		return gson.toJson(element);
	}

	/**
	 * Realiza una solicitud GET y devuelve el cuerpo de la respuesta.
	 *
	 * @param url dirección del servicio externo
	 * @return cuerpo de la respuesta
	 * @throws IOException          si falla la conexión o el servicio devuelve un
	 *                              error HTTP
	 * @throws InterruptedException si se interrumpe la solicitud
	 */
	public static String doGet(String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).timeout(Duration.ofSeconds(5))
				.header("Accept", "application/json").build();

		HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			throw new IOException("El servicio externo respondió con estado HTTP " + response.statusCode());
		}

		return response.body();
	}
}
