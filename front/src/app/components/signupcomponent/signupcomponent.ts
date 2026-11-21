import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Authservice } from '../../services/authservice';

@Component({
  selector: 'app-signupcomponent',
  standalone: false,
  styleUrl: './signupcomponent.css',
  templateUrl: './signupcomponent.html',
})
export class Signupcomponent {
  private authservice = inject(Authservice);
  private router = inject(Router);

  usuario = '';
  correo = '';
  contrasena = '';
  genero = '';

  mensaje = '';
  esError = false;
  cargando = false;
  confirmarContrasena = '';

  crearCuenta(): void {
    this.mensaje = '';
    this.esError = false;

    const usernameValido = /^(?=.*\d)(?=.*[^\p{L}\p{N}\s])\S{15}$/u.test(this.usuario);

    const passwordValida = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\p{L}\p{N}\s]).{8,15}$/u.test(
      this.contrasena,
    );

    if (!usernameValido) {
      this.esError = true;
      this.mensaje =
        'El usuario debe tener 15 caracteres, incluir un número y un símbolo, y no tener espacios.';
      return;
    }

    if (!passwordValida) {
      this.esError = true;
      this.mensaje =
        'La contraseña debe tener 8 a 15 caracteres, minúscula, mayúscula, número y símbolo.';
      return;
    }

    if (this.contrasena !== this.confirmarContrasena) {
      this.esError = true;
      this.mensaje = 'Las contraseñas no coinciden.';
      return;
    }

    this.cargando = true;

      this.authservice.registrar({
        username: this.usuario,
        email: this.correo,
        password: this.contrasena,
        gender: this.genero === 'mujer' ? 'FEMALE' : 'MALE',
      })
      .subscribe({
        next: (respuesta) => {
          this.cargando = false;
          this.esError = false;
          this.mensaje = respuesta.message;

          this.router.navigate(['/verifyemail'], {
            queryParams: { correo: this.correo },
          });
        },
        error: (error: HttpErrorResponse) => {
          this.cargando = false;
          this.esError = true;
          this.mensaje = this.authservice.obtenerMensajeError(error);
        },
      });
  }
}
