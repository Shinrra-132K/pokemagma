import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Registerrequestmodel } from '../models/registerrequestmodel';
import { Loginrequestmodel } from '../models/loginrequestmodel';
import { Loginresponsemodel } from '../models/loginresponsemodel';
import { Verifyrequestmodel } from '../models/verifyrequestmodel';
import { Messageresponsemodel } from '../models/messageresponsemodel';


@Injectable({ providedIn: 'root' })
export class Authservice {
  private http = inject(HttpClient);
  private url = 'http://localhost:8080/auth';
  private claveToken = 'pokemagma_token';

  registrar(datos: Registerrequestmodel): Observable<Messageresponsemodel> {
    return this.http.post<Messageresponsemodel>(`${this.url}/register`, datos);
  }

  verificarCorreo(datos: Verifyrequestmodel): Observable<Messageresponsemodel> {
    return this.http.post<Messageresponsemodel>(`${this.url}/verify-email`, datos);
  }

  reenviarCodigo(email: string): Observable<Messageresponsemodel> {
    return this.http.post<Messageresponsemodel>(`${this.url}/resend-code`, { email });
  }

  iniciarSesion(datos: Loginrequestmodel): Observable<Loginresponsemodel> {
    return this.http.post<Loginresponsemodel>(`${this.url}/login`, datos).pipe(
      tap(respuesta => localStorage.setItem(this.claveToken, respuesta.token))
    );
  }

  obtenerToken(): string | null {
    return localStorage.getItem(this.claveToken);
  }

  estaAutenticado(): boolean {
    return this.obtenerToken() !== null;
  }

  cerrarSesion(): void {
    localStorage.removeItem(this.claveToken);
  }

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

  solicitarCodigoRestablecimiento(
    email: string
  ): Observable<Messageresponsemodel> {
    return this.http.post<Messageresponsemodel>(
      `${this.url}/forgot-password`,
      { email }
    );
  }

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
