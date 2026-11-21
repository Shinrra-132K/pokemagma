import { Component, inject, OnInit } from '@angular/core';
import { Authservice } from '../../services/authservice';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-changepasswordcomponent',
  standalone: false,
  styleUrl: './changepasswordcomponent.css',
  templateUrl: './changepasswordcomponent.html',
})
export class Changepasswordcomponent implements OnInit {
  private authservice = inject(Authservice);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  correo = '';
  codigo = '';
  nuevaContrasena = '';
  confirmarContrasena = '';

  mensaje = '';
  esError = false;
  cargando = false;

  ngOnInit(): void {
    this.correo = this.route.snapshot.queryParamMap.get('correo') ?? '';
  }

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
