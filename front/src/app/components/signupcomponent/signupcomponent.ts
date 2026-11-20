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

  crearCuenta(): void {
    this.mensaje = '';
    this.cargando = true;

    this.authservice.registrar({
      username: this.usuario,
      email: this.correo,
      password: this.contrasena
    }).subscribe({
      next: respuesta => {
        this.cargando = false;
        this.esError = false;
        this.mensaje = respuesta.message;
        this.router.navigate(['/verify-email'], { queryParams: { correo: this.correo } });
      },
      error: (error: HttpErrorResponse) => {
        this.cargando = false;
        this.esError = true;
        this.mensaje = this.authservice.obtenerMensajeError(error);
      }
    });
  }
}
