import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Authservice } from '../../services/authservice';

@Component({
  selector: 'app-verifyemailcomponent',
  standalone: false,
  styleUrl: './verifyemailcomponent.css',
  templateUrl: './verifyemailcomponent.html',
})
export class Verifyemailcomponent implements OnInit {
  private authservice = inject(Authservice);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  correo = '';
  codigo = '';

  mensaje = '';
  esError = false;
  cargando = false;

  ngOnInit(): void {
    this.correo = this.route.snapshot.queryParamMap.get('correo') ?? '';
  }

  verificar(): void {
    this.mensaje = '';
    this.cargando = true;

    this.authservice.verificarCorreo({ email: this.correo, code: this.codigo }).subscribe({
      next: respuesta => {
        this.cargando = false;
        this.esError = false;
        this.mensaje = respuesta.message;
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (error: HttpErrorResponse) => {
        this.cargando = false;
        this.esError = true;
        this.mensaje = this.authservice.obtenerMensajeError(error);
      }
    });
  }

  reenviar(): void {
    this.mensaje = '';
    this.authservice.reenviarCodigo(this.correo).subscribe({
      next: respuesta => {
        this.esError = false;
        this.mensaje = respuesta.message;
      },
      error: (error: HttpErrorResponse) => {
        this.esError = true;
        this.mensaje = this.authservice.obtenerMensajeError(error);
      }
    });
  }}
