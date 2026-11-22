import {
  Component,
  HostListener,
  OnDestroy,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Musicservice } from '../../services/musicservice';
import { Playergameservice } from '../../services/playergameservice';
import {
  Pokeapiservice,
  PokemonInicial,
} from '../../services/pokeapiservice';

/**
 * Gestiona la introducción narrativa y la selección del Pokémon inicial.
 *
 * Obtiene del back las opciones aleatorias del jugador, consulta sus nombres
 * y sprites en PokéAPI y guarda en el back el Pokémon elegido.
 */
@Component({
  selector: 'app-introcomponent',
  standalone: false,
  templateUrl: './introcomponent.html',
  styleUrl: './introcomponent.css',
})
export class Introcomponent implements OnInit, OnDestroy {
  /** Servicio de navegación de Angular. */
  private router = inject(Router);

  /** Servicio encargado de reproducir la música de la introducción. */
  private musica = inject(Musicservice);

  /** Servicio que consulta la oferta y registra la selección en el back. */
  private playergameservice = inject(Playergameservice);

  /** Servicio que consulta nombres y sprites en PokéAPI. */
  private pokeapiservice = inject(Pokeapiservice);

  /** Diálogos introductorios del Profesor Abedul. */
  lineasAntesPokedex: string[] = [
    '¡Hola! ¡Bienvenido al mundo de los Pokémon!',
    'Me llamo Abedul, aunque todos me conocen como el Profesor Abedul.',
    'Este amigo de aquí es un pokemón, el mundo está lleno de estas criaturas.',
    'Algunas personas los tienen como compañeros. Otras los entrenan para combatir.',
    'Y hay quienes simplemente viven junto a ellos, como buenos amigos.',
    'Los Pokémon están en todas partes: en la hierba alta, en las cuevas, en el mar...',
    'Yo me dedico a investigar cómo viven y cómo se relacionan con las personas.',
  ];

  /** Texto que se muestra cuando el profesor entrega la Pokédex. */
  lineaPokedex =
    'Toma, esta es una Pokédex. Registrará automáticamente los datos de los Pokémon que veas o captures.';

  /** Texto que introduce la selección del Pokémon inicial. */
  lineaPedirInicial =
    'Ahora, dentro de este maletín tengo tres Pokébolas. ¡Elige al Pokémon que te acompañará!';

  /** Diálogo final antes de entrar al mapa. */
  lineaDespedida =
    '¡Explora por las rutas y otras ciudades para encontrar pokemon y combatir con otros entrenadores. ¡Mucha suerte!';

  /** Pokémon ofrecidos por el back, con sus datos y sprites obtenidos de PokéAPI. */
  iniciales: PokemonInicial[] = [];

  /** Indica si se están cargando las opciones de Pokémon. */
  cargandoIniciales = true;

  /** Indica si se está guardando la elección en el back. */
  cargandoSeleccion = false;

  /** Mensaje mostrado si falla la carga de las opciones. */
  errorIniciales = '';

  /** Mensaje mostrado si falla el guardado de la elección. */
  errorSeleccion = '';

  /**
   * Fase actual de la secuencia:
   * previo → pokedex → seleccion → confirmado → despedida.
   */
  fase = signal<
    'previo' | 'pokedex' | 'seleccion' | 'confirmado' | 'despedida'
  >('previo');

  /** Índice de la línea actual dentro de `lineasAntesPokedex`. */
  indice = signal(0);

  /** Texto visible actualmente en el cuadro de diálogo. */
  textoMostrado = signal('');

  /** Indica si el texto se está escribiendo progresivamente. */
  escribiendo = signal(false);

  /** Controla la visibilidad del sprite del Profesor Abedul. */
  mostrarImagen = signal(true);

  /** Controla la visibilidad del sprite del Pokémon en la escena. */
  mostrarPokemon = signal(true);

  /** Controla la visibilidad de la imagen del maletín. */
  mostrarMaleta = signal(true);

  /** Controla la animación de salida hacia el mapa. */
  saliendo = signal(false);

  /** Pokémon elegido por el jugador; es `null` antes de seleccionar. */
  inicialSeleccionado = signal<PokemonInicial | null>(null);

  /** Sprite que se muestra fuera de la fase de selección. */
  pokemonActualSprite = signal('/assets/sprites/normal/001.png');

  /** Temporizador del efecto de escritura de los diálogos. */
  private temporizador: ReturnType<typeof setInterval> | undefined;

  /** Temporizador de navegación al mapa. */
  private salida: ReturnType<typeof setTimeout> | undefined;

  /**
   * Inicia la música, el primer diálogo y la carga de la oferta inicial.
   */
  ngOnInit(): void {
    this.musica.reproducirCuandoSePueda('/assets/music/abedulscene.ogg');
    this.escribirTexto(this.lineasAntesPokedex[0]);
    this.cargarOpcionesIniciales();
  }

  /**
   * Limpia los temporizadores cuando se destruye el componente.
   */
  ngOnDestroy(): void {
    clearInterval(this.temporizador);
    clearTimeout(this.salida);
  }

  /**
   * Carga del back los IDs aleatorios y luego obtiene los Pokémon en PokéAPI.
   */
  private cargarOpcionesIniciales(): void {
    this.cargandoIniciales = true;
    this.errorIniciales = '';

    this.playergameservice.obtenerOpcionesIniciales().subscribe({
      next: oferta => {
        if (oferta.selected) {
          this.cargandoIniciales = false;
          this.router.navigate(['/game']);
          return;
        }

        const ids = [
          oferta.generationOneSpeciesId,
          oferta.generationTwoSpeciesId,
          oferta.generationThreeSpeciesId,
        ];

        forkJoin(ids.map(id => this.pokeapiservice.obtenerPokemon(id))).subscribe({
          next: pokemones => {
            this.iniciales = pokemones;
            this.cargandoIniciales = false;
          },
          error: () => {
            this.errorIniciales =
              'No se pudieron cargar los Pokémon. Intenta recargar la página.';
            this.cargandoIniciales = false;
          },
        });
      },
      error: () => {
        this.errorIniciales =
          'No se pudo obtener la oferta inicial del servidor.';
        this.cargandoIniciales = false;
      },
    });
  }

  /**
   * Permite avanzar los diálogos con X o Espacio.
   * Durante la selección, el jugador elige haciendo clic en una Pokébola.
   *
   * @param event evento de teclado capturado en la ventana.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionar(event: KeyboardEvent): void {
    if ((event.key === 'x' || event.key === ' ') && !event.repeat) {
      event.preventDefault();

      if (this.fase() !== 'seleccion') {
        this.avanzar();
      }
    }
  }

  /**
   * Guarda en el back el Pokémon seleccionado.
   * Después de que el back confirma la operación, revela su sprite y nombre.
   *
   * @param opcion Pokémon elegido.
   * @param event evento de clic que se detiene para no avanzar el diálogo.
   */
  seleccionarInicial(opcion: PokemonInicial, event: MouseEvent): void {
    event.stopPropagation();

    if (this.fase() !== 'seleccion' || this.cargandoSeleccion) {
      return;
    }

    this.cargandoSeleccion = true;
    this.errorSeleccion = '';

    this.playergameservice.elegirInicial(opcion.id).subscribe({
      next: () => {
        this.cargandoSeleccion = false;
        this.inicialSeleccionado.set(opcion);
        this.pokemonActualSprite.set(opcion.sprite);
        this.mostrarPokemon.set(true);
        this.fase.set('confirmado');
        this.escribirTexto(
          `¡Has elegido a ${opcion.nombre}! Es un gran compañero.`,
        );
      },
      error: () => {
        this.cargandoSeleccion = false;
        this.errorSeleccion =
          'No se pudo guardar la elección. Inténtalo de nuevo.';
      },
    });
  }

  /**
   * Avanza los diálogos y las fases de la introducción.
   * Al terminar la despedida, navega a la pantalla del mapa.
   */
  avanzar(): void {
    if (this.saliendo() || this.cargandoSeleccion) {
      return;
    }

    const textoActualObjetivo = this.obtenerTextoObjetivoActual();

    if (this.escribiendo() && textoActualObjetivo !== null) {
      clearInterval(this.temporizador);
      this.textoMostrado.set(textoActualObjetivo);
      this.escribiendo.set(false);
      return;
    }

    const faseActual = this.fase();

    if (faseActual === 'previo') {
      if (this.indice() < this.lineasAntesPokedex.length - 1) {
        this.indice.update(indice => indice + 1);
        this.escribirTexto(this.lineasAntesPokedex[this.indice()]);
      } else {
        this.fase.set('pokedex');
        this.escribirTexto(this.lineaPokedex);
      }

      return;
    }

    if (faseActual === 'pokedex') {
      this.fase.set('seleccion');
      this.mostrarPokemon.set(false);
      this.escribirTexto(this.lineaPedirInicial);
      return;
    }

    if (faseActual === 'seleccion') {
      // El jugador debe elegir una de las Pokébolas.
      return;
    }

    if (faseActual === 'confirmado') {
      this.fase.set('despedida');
      this.escribirTexto(this.lineaDespedida);
      return;
    }

    if (faseActual === 'despedida') {
      this.saliendo.set(true);
      this.salida = setTimeout(() => {
        this.router.navigate(['/game']);
      }, 600);
    }
  }

  /**
   * Obtiene el texto completo que corresponde a la fase activa.
   *
   * @returns diálogo esperado o `null` si la fase no tiene un diálogo.
   */
  private obtenerTextoObjetivoActual(): string | null {
    switch (this.fase()) {
      case 'previo':
        return this.lineasAntesPokedex[this.indice()] ?? '';
      case 'pokedex':
        return this.lineaPokedex;
      case 'seleccion':
        return this.lineaPedirInicial;
      case 'confirmado': {
        const seleccionado = this.inicialSeleccionado();

        return seleccionado
          ? `¡Has elegido a ${seleccionado.nombre}! Es un gran compañero.`
          : '';
      }
      case 'despedida':
        return this.lineaDespedida;
      default:
        return null;
    }
  }

  /**
   * Escribe el diálogo letra por letra y cancela cualquier escritura anterior.
   *
   * @param linea texto que se mostrará progresivamente.
   */
  private escribirTexto(linea: string): void {
    clearInterval(this.temporizador);

    this.textoMostrado.set('');
    this.escribiendo.set(true);

    let indiceCaracter = 0;

    this.temporizador = setInterval(() => {
      indiceCaracter++;
      this.textoMostrado.set(linea.slice(0, indiceCaracter));

      if (indiceCaracter >= linea.length) {
        clearInterval(this.temporizador);
        this.escribiendo.set(false);
      }
    }, 25);
  }
}
