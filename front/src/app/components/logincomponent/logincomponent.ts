import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Authservice } from '../../services/authservice';
import { Playergameservice } from '../../services/playergameservice';

/**
 * Gestiona el inicio de sesión y decide a qué pantalla navegar
 * según si el jugador ya tiene Pokémon en su equipo.
 */
@Component({
  selector: 'app-logincomponent',
  standalone: false,
  styleUrl: './logincomponent.css',
  templateUrl: './logincomponent.html',
})
export class Logincomponent {
  /** Servicio que envía las credenciales al back y guarda el token recibido. */
  private authservice = inject(Authservice);

  /** Servicio de navegación entre las pantallas de Angular. */
  private router = inject(Router);

  /** Servicio que consulta el equipo del jugador autenticado. */
  private playergameservice = inject(Playergameservice);

  /** Nombre de usuario ingresado en el formulario. */
  usuario = '';

  /** Contraseña ingresada en el formulario. */
  contrasena = '';

  /** Mensaje informativo o de error mostrado en la pantalla. */
  mensaje = '';

  /** Indica si el mensaje actual corresponde a un error. */
  esError = false;

  /** Indica si el login o la consulta del equipo está en curso. */
  cargando = false;

  /**
   * Envía las credenciales al back.
   *
   * Si el login es exitoso, consulta el equipo:
   * - Sin Pokémon: navega a la intro y selección del inicial.
   * - Con Pokémon: navega directamente al mapa.
   *
   * Si el correo aún no está verificado, redirige a la pantalla de verificación.
   */
  ingresar(): void {
    this.mensaje = '';
    this.esError = false;
    this.cargando = true;

    this.authservice.iniciarSesion({
      username: this.usuario,
      password: this.contrasena,
    }).subscribe({
      next: () => {
        // Authservice ya guardó el token antes de emitir la respuesta.
        this.playergameservice.obtenerEquipo().subscribe({
          next: equipo => {
            this.cargando = false;

            if (!equipo || equipo.length === 0) {
              this.router.navigate(['/intro']);
            } else {
              this.router.navigate(['/game']);
            }
          },
          error: (error: HttpErrorResponse) => {
            this.cargando = false;
            this.esError = true;
            this.mensaje = this.authservice.obtenerMensajeError(error);
          },
        });
      },
      error: (error: HttpErrorResponse) => {
        this.cargando = false;
        this.esError = true;

        if (error.status === 403) {
          this.mensaje = 'Debes verificar tu correo antes de ingresar.';
          this.router.navigate(['/verifyemail']);
        } else {
          this.mensaje = this.authservice.obtenerMensajeError(error);
        }
      },
    });
  }
}
