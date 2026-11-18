package co.edu.unbosque.pokemagma.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envia correos electronicos a las cuentas del juego
 */
@Service
public class EmailService {
	
	/**
	 * Cliente de Spring para enviar correos
	 */
	private final JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	private String from;

	/**
	 * Constructor con parametros
	 * @param mailSender libreria encargada del envio de los correos
	 */
	public EmailService(JavaMailSender mailSender) {
		super();
		this.mailSender = mailSender;
	}
	
	/**
	 * Envia el codigo de verificacion al correo del usuario
	 */
	public void sendVerificationCode(String to, String username, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject("Pokemagma - Codigo de verificacion");
		message.setText("Hola " + username + ",\n\n" 
				+ "Tu codigo de verificacion es: " + code + "\n\n"
				+ "Es valido por 10 minutos. Si no creaste esta cuenta, ignora este mensaje.");
		mailSender.send(message);
	}

}
