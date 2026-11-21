import { Component, inject, OnInit } from '@angular/core';
import { Authservice } from '../../services/authservice';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

/**
 * Gestiona el formulario para cambiar la contraseña usando el correo
 * y el código de recuperación.
 */
@Component({
  selector: 'app-changepasswordcomponent',
  standalone: false,
  styleUrl: './changepasswordcomponent.css',
  templateUrl: './changepasswordcomponent.html',
})
export class Changepasswordcomponent implements OnInit {
  /** Servicio que realiza las solicitudes de autenticación al back. */
  private authservice = inject(Authservice);

  /** Ruta activa, usada para leer el correo enviado desde la pantalla anterior. */
  private route = inject(ActivatedRoute);

  /** Servicio para navegar a otra ruta después de cambiar la contraseña. */
  private router = inject(Router);

  /** Correo asociado a la solicitud de recuperación. */
  correo = '';

  /** Código de recuperación recibido por correo. */
  codigo = '';

  /** Nueva contraseña que se enviará al back. */
  nuevaContrasena = '';

  /** Confirmación de la nueva contraseña para validarla en el formulario. */
  confirmarContrasena = '';

  /** Mensaje informativo o de error que se muestra en la pantalla. */
  mensaje = '';

  /** Indica si el mensaje mostrado corresponde a un error. */
  esError = false;

  /** Indica si hay una solicitud de cambio de contraseña en curso. */
  cargando = false;

  /**
   * Lee el correo enviado como parámetro de consulta en la ruta.
   * Si no existe, asigna una cadena vacía.
   */
  ngOnInit(): void {
    this.correo = this.route.snapshot.queryParamMap.get('correo') ?? '';
  }

  /**
   * Valida que las contraseñas coincidan y cumplan los requisitos.
   * Envía el correo, el código y la contraseña nueva al back.
   * Si el cambio es exitoso, muestra la respuesta y navega al login;
   * si ocurre un error, muestra el mensaje correspondiente.
   */
  cambiarContrasena(): void {
    this.mensaje = '';
    this.esError = false;

    if (this.nuevaContrasena !== this.confirmarContrasena) {
      this.mensaje = 'Las contraseñas no coinciden.';
      this.esError = true;
      return;
    }

    const passwordValida = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\p{L}\p{N}\s]).{8,15}$/u.test(
      this.nuevaContrasena,
    );

    if (!passwordValida) {
      this.mensaje = 'Usa de 8 a 15 caracteres con minúscula, mayúscula, número y símbolo.';
      this.esError = true;
      return;
    }

    this.cargando = true;

    this.authservice
      .restablecerContrasena({
        email: this.correo,
        code: this.codigo,
        newPassword: this.nuevaContrasena,
      })
      .subscribe({
        next: (respuesta) => {
          this.mensaje = respuesta.message;
          this.esError = false;
          this.cargando = false;

          setTimeout(() => this.router.navigate(['/login']), 1500);
        },
        error: (error: HttpErrorResponse) => {
          this.mensaje = this.authservice.obtenerMensajeError(error);
          this.esError = true;
          this.cargando = false;
        },
      });
  }
}
