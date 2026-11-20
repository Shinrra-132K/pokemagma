import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Authservice } from '../../services/authservice';

/**
 * Componente encargado de gestionar el inicio de sesión de los usuarios.
 * Valida las credenciales contra el servicio de autenticación, controla los estados
 * de carga y error en la interfaz, y redirige según el resultado de la respuesta.
 */
@Component({
  selector: 'app-logincomponent',
  standalone: false,
  styleUrl: './logincomponent.css',
  templateUrl: './logincomponent.html',
})
export class Logincomponent {
  /** Servicio de autenticación utilizado para enviar las credenciales y procesar errores HTTP. */
  private authservice = inject(Authservice);

  /** Servicio de enrutamiento para redirigir al usuario tras iniciar sesión o requerir verificación. */
  private router = inject(Router);

  /** Nombre de usuario ingresado en el formulario de acceso. */
  usuario = '';

  /** Contraseña ingresada en el formulario de acceso. */
  contrasena = '';

  /** Mensaje de retroalimentación o error que se muestra en la interfaz. */
  mensaje = '';

  /** Indica si el mensaje actual corresponde a un estado de error (`true`) o informativo (`false`). */
  esError = false;

  /** Indica si existe una petición de inicio de sesión en curso para bloquear envíos duplicados o mostrar carga. */
  cargando = false;

  /**
   * Envía las credenciales al servicio de autenticación para iniciar sesión.
   *
   * - Si la autenticación es exitosa, detiene el estado de carga y redirige a `/howtoplay`.
   * - Si el servidor responde con estado `403` (correo sin verificar), muestra el aviso correspondiente
   *   y redirige a `/verifyemail`.
   * - Ante cualquier otro error HTTP, delega la extracción del mensaje legible a `Authservice`.
   */
  ingresar(): void {
    this.mensaje = '';
    this.cargando = true;

    this.authservice.iniciarSesion({
      username: this.usuario,
      password: this.contrasena
    }).subscribe({
      next: () => {
        this.cargando = false;
        this.router.navigate(['/howtoplay']); // aqui cambio a intro cuando tenga la logica funcional
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
      }
    });
  }
}
