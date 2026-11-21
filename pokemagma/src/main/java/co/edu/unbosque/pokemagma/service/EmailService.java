package co.edu.unbosque.pokemagma.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

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
	 * 
	 * @param mailSender libreria encargada del envio de los correos
	 */
	public EmailService(JavaMailSender mailSender) {
		super();
		this.mailSender = mailSender;
	}

	public void sendVerificationCode(String to, String username, String code) {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject("Pokemagma | Verifica tu cuenta");

			String html = """
					<!DOCTYPE html>
					<html lang="es">
					<head>
					  <meta charset="UTF-8">
					  <meta name="viewport" content="width=device-width, initial-scale=1.0">
					  <title>Verificación de cuenta</title>
					</head>
					<body style="margin:0;padding:0;background-color:#eef2f7;font-family:Arial,Helvetica,sans-serif;color:#172033;">
					  <div style="display:none;max-height:0;overflow:hidden;opacity:0;">
					    Tu código de entrenador para Pokemagma es %s
					  </div>

					  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0"
					         style="background-color:#eef2f7;padding:32px 12px;">
					    <tr>
					      <td align="center">
					        <table role="presentation" width="600" cellspacing="0" cellpadding="0" border="0"
					               style="max-width:600px;background:#ffffff;border-radius:20px;overflow:hidden;">
					          <tr>
					            <td align="center" style="padding:30px 24px;background:#f5c400;color:#302600;">
					              <div style="font-size:13px;letter-spacing:3px;text-transform:uppercase;">
					                Liga Pokemagma
					              </div>
					              <h1 style="margin:10px 0 0;font-size:30px;line-height:1.2;">
					                ¡Tu aventura comienza!
					              </h1>
					            </td>
					          </tr>

					          <tr>
					            <td style="padding:34px 32px 20px;">
					              <p style="margin:0 0 14px;font-size:18px;">
					                Hola, <strong>%s</strong>
					              </p>
					              <p style="margin:0;font-size:15px;line-height:1.7;color:#475569;">
					                Para verificar tu correo y activar tu cuenta de entrenador,
					                ingresa este código:
					              </p>

					              <div style="margin:26px 0;padding:20px 12px;text-align:center;
					                          background:#fffbea;border:2px solid #f5c400;border-radius:14px;">
					                <div style="font-size:12px;letter-spacing:2px;text-transform:uppercase;color:#806500;">
					                  Código de verificación
					                </div>
					                <div style="margin-top:8px;font-size:36px;font-weight:bold;letter-spacing:10px;color:#b38f00;">
					                  %s
					                </div>
					              </div>

					              <p style="margin:0;font-size:14px;line-height:1.7;color:#475569;">
					                Este código vence en <strong>10 minutos</strong>.
					              </p>
					              <p style="margin:18px 0 0;font-size:13px;line-height:1.6;color:#64748b;">
					                Si no solicitaste crear una cuenta, ignora este mensaje.
					              </p>
					            </td>
					          </tr>

					          <tr>
					            <td align="center" style="padding:20px 24px;background:#f8fafc;color:#94a3b8;font-size:12px;">
					              Pokemagma
					            </td>
					          </tr>
					        </table>
					      </td>
					    </tr>
					  </table>
					</body>
					</html>
					"""
					.formatted(escapeHtml(code), escapeHtml(username), escapeHtml(code));

			helper.setText(html, true);
			mailSender.send(message);
		} catch (MessagingException exception) {
			throw new IllegalStateException("No se pudo crear el correo de verificación.", exception);
		}
	}

	private String escapeHtml(String value) {
		if (value == null) {
			return "";
		}

		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	/**
	 * Envía el código de restablecimiento de contraseña.
	 *
	 * @param to       correo del destinatario
	 * @param username nombre del usuario
	 * @param code     código temporal
	 */
	public void sendPasswordResetCode(String to, String username, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject("Pokemagma - Restablecimiento de contraseña");
		message.setText("Hola " + username + ",\n\n" + "Tu código para restablecer la contraseña es: " + code + "\n\n"
				+ "Es válido por 10 minutos. Si no solicitaste el cambio, ignora este mensaje.");

		mailSender.send(message);
	}

}
