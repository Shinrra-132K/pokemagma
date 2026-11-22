import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Authservice } from '../../services/authservice';
import { Playergameservice } from '../../services/playergameservice';

@Component({
  selector: 'app-logincomponent',
  standalone: false,
  styleUrl: './logincomponent.css',
  templateUrl: './logincomponent.html',
})
export class Logincomponent {
  private authservice = inject(Authservice);
  private router = inject(Router);
  private playergameservice = inject(Playergameservice);

  usuario = '';
  contrasena = '';
  mensaje = '';
  esError = false;
  cargando = false;

  ingresar(): void {
    this.mensaje = '';
    this.esError = false;
    this.cargando = true;

    this.authservice.iniciarSesion({
      username: this.usuario,
      password: this.contrasena,
    }).subscribe({
      next: () => {
        // El servicio guarda el token antes de emitir la respuesta.
        if (this.authservice.esAdministrador()) {
          this.cargando = false;
          void this.router.navigate(['/admin']);
          return;
        }

        this.playergameservice.obtenerEquipo().subscribe({
          next: equipo => {
            this.cargando = false;

            if (!equipo || equipo.length === 0) {
              void this.router.navigate(['/intro']);
            } else {
              void this.router.navigate(['/game']);
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
          void this.router.navigate(['/verifyemail']);
        } else {
          this.mensaje = this.authservice.obtenerMensajeError(error);
        }
      },
    });
  }
}
