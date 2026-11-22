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
  signal,
} from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { Pokedexmodel } from '../../models/pokedexmodel';
import { Pokeapiservice } from '../../services/pokeapiservice';

interface EntradaPokedexApi {
  speciesId: number;
  seen: boolean;
  caught: boolean;
}

@Component({
  selector: 'app-pokedexcomponent',
  standalone: false,
  styleUrl: './pokedexcomponent.css',
  templateUrl: './pokedexcomponent.html',
})
export class Pokedexcomponent implements OnChanges, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly pokeApi = inject(Pokeapiservice);
  private readonly urlPokedex = 'http://localhost:8080/player/pokedex/getall';

  @Input() abierto = false;

  @Output() readonly cerrar = new EventEmitter<void>();

  /** Solo contiene especies que el jugador ya capturó. */
  entradas: Pokedexmodel[] = [];

  readonly indiceSeleccionado = signal(0);

  cargando = false;
  error = '';

  private solicitud?: Subscription;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['abierto']?.currentValue === true) {
      this.indiceSeleccionado.set(0);
      this.cargarCapturados();
    }

    if (changes['abierto']?.currentValue === false) {
      this.solicitud?.unsubscribe();
    }
  }

  ngOnDestroy(): void {
    this.solicitud?.unsubscribe();
  }

  private cargarCapturados(): void {
    const token = localStorage.getItem('pokemagma_token');

    if (!token) {
      this.error = 'No se encontró la sesión. Inicia sesión nuevamente.';
      this.entradas = [];
      return;
    }

    this.cargando = true;
    this.error = '';

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    this.solicitud?.unsubscribe();
    this.solicitud = this.http
      .get<EntradaPokedexApi[] | null>(this.urlPokedex, { headers })
      .subscribe({
        next: registros => {
          const capturados = (registros ?? []).filter(
            registro => registro.caught
          );

          if (capturados.length === 0) {
            this.entradas = [];
            this.indiceSeleccionado.set(0);
            this.cargando = false;
            return;
          }

          this.cargarNombres(capturados);
        },
        error: error => {
          this.entradas = [];
          this.cargando = false;

          if (error.status === 401 || error.status === 403) {
            this.error =
              'No tienes permiso para consultar esta Pokédex. Inicia sesión como jugador.';
          } else if (error.status === 0) {
            this.error =
              'No se pudo conectar con el backend en http://localhost:8080.';
          } else {
            this.error = `No se pudo cargar la Pokédex (error ${error.status}).`;
          }
        },
      });
  }

  private cargarNombres(registros: EntradaPokedexApi[]): void {
    const consultas = registros.map(registro =>
      this.pokeApi.obtenerPokemon(registro.speciesId).pipe(
        catchError(() =>
          of({
            id: registro.speciesId,
            nombre: `Pokémon ${registro.speciesId}`,
            sprite: '',
            pokeball: '',
          })
        )
      )
    );

    this.solicitud = forkJoin(consultas).subscribe({
      next: pokemon => {
        this.entradas = pokemon
          .map(item => ({
            numero: item.id,
            nombre: this.formatearNombre(item.nombre),
            visto: true,
            capturado: true,
            tipo: 'Capturado',
            descripcion: `ID de especie: ${item.id}`,
            sprite: item.sprite || undefined,
          }))
          .sort((a, b) => a.numero - b.numero);

        this.indiceSeleccionado.set(0);
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar los nombres de los Pokémon.';
        this.cargando = false;
      },
    });
  }

  private formatearNombre(nombre: string): string {
    return nombre
      .split('-')
      .map(parte => parte.charAt(0).toUpperCase() + parte.slice(1))
      .join(' ');
  }

  @HostListener('window:keydown', ['$event'])
  alPresionarTecla(evento: KeyboardEvent): void {
    if (!this.abierto || this.cargando) {
      return;
    }

    const totalFilas = this.entradas.length + 1;

    if (evento.key === 'ArrowUp') {
      evento.preventDefault();
      evento.stopPropagation();

      this.indiceSeleccionado.set(
        (this.indiceSeleccionado() - 1 + totalFilas) % totalFilas
      );
    } else if (evento.key === 'ArrowDown') {
      evento.preventDefault();
      evento.stopPropagation();

      this.indiceSeleccionado.set(
        (this.indiceSeleccionado() + 1) % totalFilas
      );
    } else if (evento.key === 'Escape' && !evento.repeat) {
      evento.preventDefault();
      evento.stopPropagation();
      this.cerrar.emit();
    }
  }

  esFilaSalir(): boolean {
    return this.indiceSeleccionado() >= this.entradas.length;
  }

  obtenerEntradaActual(): Pokedexmodel | undefined {
    if (this.esFilaSalir()) {
      return undefined;
    }

    return this.entradas[this.indiceSeleccionado()];
  }

  totalCapturados(): number {
    return this.entradas.length;
  }

  totalVistos(): number {
    return this.entradas.length;
  }

  confirmarFila(indice: number): void {
    this.indiceSeleccionado.set(indice);

    if (indice >= this.entradas.length) {
      this.cerrar.emit();
    }
  }

  reintentar(): void {
    this.cargarCapturados();
  }
}
