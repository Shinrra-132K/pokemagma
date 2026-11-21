import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Authservice } from '../../services/authservice';

/**
 * Gestiona la solicitud de un código para recuperar la contraseña.
 * Cuando el back procesa la solicitud, navega al formulario de cambio.
 */
@Component({
  selector: 'app-forgottenpasswordcomponent',
  standalone: false,
  styleUrl: './forgottenpasswordcomponent.css',
  templateUrl: './forgottenpasswordcomponent.html',
})
export class Forgottenpasswordcomponent {
  /** Servicio que realiza las solicitudes de recuperación al back. */
  private authservice = inject(Authservice);

  /** Servicio para navegar al formulario de cambio de contraseña. */
  private router = inject(Router);

  /** Correo al que se solicita enviar el código de recuperación. */
  correo = '';

  /** Mensaje informativo o de error que se muestra en la pantalla. */
  mensaje = '';

  /** Indica si el mensaje mostrado corresponde a un error. */
  esError = false;

  /** Indica si hay una solicitud de código en curso. */
  cargando = false;

  /**
   * Envía el correo al endpoint de recuperación.
   * Si el back responde correctamente, muestra el mensaje y después navega
   * a /change pasando el correo como parámetro de consulta.
   * Si ocurre un error HTTP, muestra el mensaje correspondiente.
   */
  solicitarCodigo(): void {
    this.mensaje = '';
    this.cargando = true;

    this.authservice.solicitarCodigoRestablecimiento(this.correo).subscribe({
      next: respuesta => {
        this.mensaje = respuesta.message;
        this.esError = false;
        this.cargando = false;

        setTimeout(() => {
          this.router.navigate(['/change'], {
            queryParams: { correo: this.correo }
          });
        }, 1500);
      },
      error: (error: HttpErrorResponse) => {
        this.mensaje = this.authservice.obtenerMensajeError(error);
        this.esError = true;
        this.cargando = false;
      }
    });
  }
}
