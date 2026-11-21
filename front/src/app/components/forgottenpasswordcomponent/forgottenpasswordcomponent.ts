import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Authservice } from '../../services/authservice';

@Component({
  selector: 'app-forgottenpasswordcomponent',
  standalone: false,
  styleUrl: './forgottenpasswordcomponent.css',
  templateUrl: './forgottenpasswordcomponent.html',
})
export class Forgottenpasswordcomponent {
  private authservice = inject(Authservice);
  private router = inject(Router);

  correo = '';
  mensaje = '';
  esError = false;
  cargando = false;

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
