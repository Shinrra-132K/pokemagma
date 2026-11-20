package co.edu.unbosque.pokemagma.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Consulta en ipapi una ubicación aproximada asociada a una IP pública.
 */
@Service
public class IpGeolocationService {

	/**
	 * Obtiene ciudad, región y país para una IP pública.
	 *
	 * @param ipAddress dirección detectada por el servidor
	 * @return ubicación aproximada o "No disponible"
	 */
	public String findLocation(String ipAddress) {
		if (ipAddress == null || ipAddress.isBlank()) {
			return "No disponible";
		}

		if (isLocalOrPrivateAddress(ipAddress)) {
			return "IP local o privada";
		}

		try {
			String url = "https://ipapi.co/" + ipAddress + "/json/";
			String response = ExternalHTTPRequestHandler.doGet(url);
			JsonObject data = JsonParser.parseString(response).getAsJsonObject();

			if (data.has("error") && data.get("error").getAsBoolean()) {
				return "No disponible";
			}

			String city = getValue(data, "city");
			String region = getValue(data, "region");
			String country = getValue(data, "country_name");

			StringBuilder location = new StringBuilder();
			appendPart(location, city);
			appendPart(location, region);
			appendPart(location, country);

			if (location.length() == 0) {
				return "No disponible";
			}

			return location.toString();
		} catch (IOException exception) {
			return "No disponible";
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			return "No disponible";
		} catch (RuntimeException exception) {
			return "No disponible";
		}
	}

	/**
	 * Comprueba si la dirección es local, privada o no válida.
	 *
	 * @param ipAddress dirección IP que se comprobará
	 * @return true si no es una IP pública utilizable para geolocalización
	 */
	private boolean isLocalOrPrivateAddress(String ipAddress) {
		try {
			InetAddress address = InetAddress.getByName(ipAddress);

			return address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
					|| address.isSiteLocalAddress() || address.isMulticastAddress();
		} catch (UnknownHostException exception) {
			return true;
		}
	}

	/**
	 * Obtiene un campo de texto de la respuesta JSON.
	 *
	 * @param data      respuesta JSON de ipapi
	 * @param fieldName campo que se consultará
	 * @return valor del campo o cadena vacía
	 */
	private String getValue(JsonObject data, String fieldName) {
		if (!data.has(fieldName) || data.get(fieldName).isJsonNull()) {
			return "";
		}

		return data.get(fieldName).getAsString();
	}

	/**
	 * Añade una parte no vacía a la ubicación.
	 *
	 * @param location texto de ubicación que se está construyendo
	 * @param part     parte que se añadirá
	 */
	private void appendPart(StringBuilder location, String part) {
		if (part == null || part.isBlank()) {
			return;
		}

		if (location.length() > 0) {
			location.append(", ");
		}

		location.append(part);
	}

}
