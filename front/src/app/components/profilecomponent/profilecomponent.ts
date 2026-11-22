import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
  inject,
} from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { forkJoin, of, Subscription } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { Playergameservice } from '../../services/playergameservice';
import { Pokeapiservice } from '../../services/pokeapiservice';

interface PerfilJugador {
  id: number;
  userId: number;
  gender: string;
  trainerAppearance: string;
}

interface PokemonPerfil {
  speciesId: number;
  nickname: string | null;
  level: number;
  name: string;
  sprite: string;
}

@Component({
  selector: 'app-profilecomponent',
  standalone: false,
  styleUrl: './profilecomponent.css',
  templateUrl: './profilecomponent.html',
})
export class Profilecomponent implements OnChanges, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly playergameservice = inject(Playergameservice);
  private readonly pokeapiservice = inject(Pokeapiservice);

  private readonly urlPerfil = 'http://localhost:8080/player/profile/get';
  private readonly urlConteoPokedex =
    'http://localhost:8080/player/pokedex/caught/count';

  @Input() abierto = false;

  @Output() readonly cerrar = new EventEmitter<void>();

  nombreUsuario = 'Entrenador';
  idUsuario: number | null = null;
  genero = 'No disponible';
  apariencia = 'brendan';

  cantidadCapturados = 0;
  equipo: PokemonPerfil[] = [];

  cargando = false;
  error = '';

  private suscripcion?: Subscription;
  private suscripcionEquipo?: Subscription;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['abierto']?.currentValue === true) {
      this.cargarDatosPerfil();
    }

    if (changes['abierto']?.currentValue === false) {
      this.suscripcion?.unsubscribe();
      this.suscripcionEquipo?.unsubscribe();
    }
  }

  ngOnDestroy(): void {
    this.suscripcion?.unsubscribe();
    this.suscripcionEquipo?.unsubscribe();
  }

  private cargarDatosPerfil(): void {
    const token = localStorage.getItem('pokemagma_token');

    if (!token) {
      this.error = 'No hay una sesión activa. Inicia sesión nuevamente.';
      return;
    }

    this.nombreUsuario = this.obtenerNombreDelToken(token);

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    this.cargando = true;
    this.error = '';

    this.suscripcion?.unsubscribe();

    this.suscripcion = forkJoin({
      perfil: this.http
        .get<PerfilJugador>(this.urlPerfil, { headers })
        .pipe(
          catchError(error => {
            this.error =
              error.status === 404
                ? 'Todavía no existe un perfil asociado a esta cuenta.'
                : this.mensajeError(error.status);
            return of(null);
          })
        ),

      capturados: this.http
        .get<number>(this.urlConteoPokedex, { headers })
        .pipe(catchError(() => of(0))),

      equipo: this.playergameservice
        .obtenerEquipo()
        .pipe(catchError(() => of(null))),
    }).subscribe({
      next: resultado => {
        this.cantidadCapturados = resultado.capturados ?? 0;

        if (resultado.perfil) {
          this.idUsuario = resultado.perfil.userId;
          this.genero = this.formatearGenero(resultado.perfil.gender);
          this.apariencia = this.obtenerApariencia(
            resultado.perfil.trainerAppearance
          );
        }

        this.cargarNombresEquipo(resultado.equipo ?? []);
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar los datos del perfil.';
        this.cargando = false;
      },
    });
  }

  private cargarNombresEquipo(
    pokemones: Array<{
      id: number;
      speciesId: number;
      nickname: string | null;
      level: number;
    }>
  ): void {
    this.suscripcionEquipo?.unsubscribe();

    const equipo = pokemones.slice(0, 6);

    if (equipo.length === 0) {
      this.equipo = [];
      return;
    }

    this.suscripcionEquipo = forkJoin(
      equipo.map(pokemon =>
        this.pokeapiservice.obtenerPokemon(pokemon.speciesId).pipe(
          catchError(() =>
            of({
              id: pokemon.speciesId,
              nombre: `Pokémon ${pokemon.speciesId}`,
              sprite: '',
              pokeball: '',
            })
          )
        )
      )
    ).subscribe({
      next: datosApi => {
        this.equipo = equipo.map((pokemon, indice) => ({
          speciesId: pokemon.speciesId,
          nickname: pokemon.nickname,
          level: pokemon.level,
          name: this.formatearNombre(
            pokemon.nickname || datosApi[indice].nombre
          ),
          sprite: datosApi[indice].sprite,
        }));
      },
      error: () => {
        this.equipo = equipo.map(pokemon => ({
          speciesId: pokemon.speciesId,
          nickname: pokemon.nickname,
          level: pokemon.level,
          name: pokemon.nickname || `Pokémon ${pokemon.speciesId}`,
          sprite: '',
        }));
      },
    });
  }

  private obtenerNombreDelToken(token: string): string {
    try {
      const partes = token.split('.');
      if (partes.length !== 3 || !partes[1]) {
        return 'Entrenador';
      }

      const base64 = partes[1]
        .replace(/-/g, '+')
        .replace(/_/g, '/');

      const base64ConRelleno = base64.padEnd(
        Math.ceil(base64.length / 4) * 4,
        '='
      );

      const bytes = Uint8Array.from(
        atob(base64ConRelleno),
        caracter => caracter.charCodeAt(0)
      );

      const claims = JSON.parse(
        new TextDecoder().decode(bytes)
      ) as { sub?: string };

      return claims.sub || 'Entrenador';
    } catch {
      return 'Entrenador';
    }
  }

  private obtenerApariencia(apariencia: string | null | undefined): string {
    const valor = (apariencia || '').toLowerCase();

    // El backend asigna "may" o "brendan" al crear la cuenta.
    return valor === 'may' || valor === 'brendan'
      ? valor
      : 'brendan';
  }

  private formatearGenero(genero: string | null | undefined): string {
    if (genero === 'FEMALE') {
      return 'Femenino';
    }

    if (genero === 'MALE') {
      return 'Masculino';
    }

    return genero || 'No disponible';
  }

  private formatearNombre(nombre: string): string {
    return nombre
      .split('-')
      .map(parte => parte.charAt(0).toUpperCase() + parte.slice(1))
      .join(' ');
  }

  private mensajeError(status: number): string {
    if (status === 0) {
      return 'No se pudo conectar con el backend en http://localhost:8080.';
    }

    if (status === 401 || status === 403) {
      return 'La sesión no está autorizada. Vuelve a iniciar sesión.';
    }

    return `No se pudo consultar el perfil (error ${status}).`;
  }

  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(evento: KeyboardEvent): void {
    if (!this.abierto) {
      return;
    }

    const tecla = evento.key.toLowerCase();

    if ((tecla === 'x' || evento.key === 'Escape') && !evento.repeat) {
      evento.preventDefault();
      evento.stopPropagation();
      this.cerrar.emit();
    }
  }
}
