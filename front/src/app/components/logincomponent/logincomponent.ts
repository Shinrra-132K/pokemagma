import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Authservice } from '../../services/authservice';


@Component({
  selector: 'app-logincomponent',
  standalone: false,
  styleUrl: './logincomponent.css',
  templateUrl: './logincomponent.html',
})
export class Logincomponent {
  private authservice = inject(Authservice);
  private router = inject(Router);

  usuario = '';
  contrasena = '';

  mensaje = '';
  esError = false;
  cargando = false;

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
