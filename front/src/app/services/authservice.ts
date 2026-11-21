import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Registerrequestmodel } from '../models/registerrequestmodel';
import { Loginrequestmodel } from '../models/loginrequestmodel';
import { Loginresponsemodel } from '../models/loginresponsemodel';
import { Verifyrequestmodel } from '../models/verifyrequestmodel';
import { Messageresponsemodel } from '../models/messageresponsemodel';

/**
 * Centraliza las solicitudes HTTP para registro, verificación de correo,
 * inicio de sesión y recuperación de contraseña.
 *
 * También administra el token de autenticación almacenado localmente.
 */
@Injectable({ providedIn: 'root' })
export class Authservice {
  /** Cliente HTTP de Angular usado para comunicarse con el back. */
  private http = inject(HttpClient);

  /** URL base de los endpoints de autenticación del back. */
  private url = 'http://localhost:8080/auth';

  /** Clave de localStorage donde se guarda el token de autenticación. */
  private claveToken = 'pokemagma_token';

  /**
   * Envía los datos de registro al back.
   *
   * Endpoint: POST /auth/register.
   *
   * @param datos nombre de usuario, correo, contraseña y género.
   * @returns Observable con el mensaje de respuesta.
   */
  registrar(datos: Registerrequestmodel): Observable<Messageresponsemodel> {
    return this.http.post<Messageresponsemodel>(`${this.url}/register`, datos);
  }

  /**
   * Envía al back el correo y el código de verificación.
   *
   * Endpoint: POST /auth/verify-email.
   *
   * @param datos correo electrónico y código de verificación.
   * @returns Observable con el mensaje de resultado.
   */
  verificarCorreo(datos: Verifyrequestmodel): Observable<Messageresponsemodel> {
    return this.http.post<Messageresponsemodel>(`${this.url}/verify-email`, datos);
  }

  /**
   * Solicita al back que reenvíe el código de verificación.
   *
   * Endpoint: POST /auth/resend-code.
   *
   * @param email correo asociado a la cuenta.
   * @returns Observable con el mensaje de respuesta.
   */
  reenviarCodigo(email: string): Observable<Messageresponsemodel> {
    return this.http.post<Messageresponsemodel>(`${this.url}/resend-code`, { email });
  }

  /**
   * Envía las credenciales para iniciar sesión.
   * Si el back responde correctamente, guarda el token recibido en localStorage.
   *
   * Endpoint: POST /auth/login.
   *
   * @param datos nombre de usuario y contraseña.
   * @returns Observable con el token y el tipo de autenticación.
   */
  iniciarSesion(datos: Loginrequestmodel): Observable<Loginresponsemodel> {
    return this.http.post<Loginresponsemodel>(`${this.url}/login`, datos).pipe(
      tap(respuesta => localStorage.setItem(this.claveToken, respuesta.token))
    );
  }

  /**
   * Obtiene el token guardado en el navegador.
   *
   * @returns El token almacenado o null si no existe.
   */
  obtenerToken(): string | null {
    return localStorage.getItem(this.claveToken);
  }

  /**
   * Comprueba si hay un token guardado.
   * No verifica si el token sigue vigente o es válido para el back.
   *
   * @returns true si existe un token almacenado; de lo contrario, false.
   */
  estaAutenticado(): boolean {
    return this.obtenerToken() !== null;
  }

  /**
   * Cierra la sesión local eliminando el token guardado en el navegador.
   */
  cerrarSesion(): void {
    localStorage.removeItem(this.claveToken);
  }

  /**
   * Obtiene un mensaje legible a partir de un error HTTP.
   *
   * @param error error HTTP recibido en una solicitud.
   * @returns Mensaje de conexión, mensaje del back o código de error.
   */
  obtenerMensajeError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor.';
    }

    const cuerpo = error.error;

    if (typeof cuerpo === 'string' && cuerpo) {
      return cuerpo;
    }

    return cuerpo?.message ?? cuerpo?.error ?? `Error ${error.status}`;
  }

  /**
   * Solicita al back un código para restablecer la contraseña.
   *
   * Endpoint: POST /auth/forgot-password.
   *
   * @param email correo asociado a la cuenta.
   * @returns Observable con el mensaje de respuesta.
   */
  solicitarCodigoRestablecimiento(
    email: string
  ): Observable<Messageresponsemodel> {
    return this.http.post<Messageresponsemodel>(
      `${this.url}/forgot-password`,
      { email }
    );
  }

  /**
   * Envía el código y la nueva contraseña para completar el restablecimiento.
   *
   * Endpoint: POST /auth/reset-password.
   *
   * @param datos correo, código recibido y contraseña nueva.
   * @returns Observable con el mensaje de resultado.
   */
  restablecerContrasena(datos: {
    email: string;
    code: string;
    newPassword: string;
  }): Observable<Messageresponsemodel> {
    return this.http.post<Messageresponsemodel>(
      `${this.url}/reset-password`,
      datos
    );
  }
}
