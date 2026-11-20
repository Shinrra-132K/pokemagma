import { Component, HostListener, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Representa una opción de Pokémon inicial disponible para seleccionar en el maletín.
 */
export interface InicialOpcion {
  /** Número de la Pokédex nacional o regional del Pokémon. */
  id: number;
  /** Nombre visible del Pokémon inicial. */
  nombre: string;
  /** Ruta del sprite normal del Pokémon que se mostrará al ser elegido. */
  sprite: string;
  /** Ruta del ícono de Pokébola mostrado en el selector. */
  pokeball: string;
}

/**
 * Componente de introducción narrativa del juego.
 * Gestiona los diálogos iniciales del Profesor Abedul, la entrega de la Pokédex,
 * la selección de uno de los tres Pokémon iniciales y la transición hacia `/game`.
 */
@Component({
  selector: 'app-introcomponent',
  standalone: false,
  templateUrl: './introcomponent.html',
  styleUrl: './introcomponent.css'
})
export class Introcomponent implements OnInit, OnDestroy {
  /** Servicio de enrutamiento para redirigir al escenario principal al finalizar la intro. */
  private router = inject(Router);

  /** Diálogos introductorios del Profesor Abedul antes de entregar la Pokédex. */
  lineasAntesPokedex: string[] = [
    '¡Hola! ¡Bienvenido al mundo de los Pokémon!',
    'Me llamo Abedul, aunque todos me conocen como el Profesor Abedul.',
    'Este amigo de aquí es un pokemón, el mundo está lleno de estas criaturas.',
    'Algunas personas los tienen como compañeros. Otras los entrenan para combatir.',
    'Y hay quienes simplemente viven junto a ellos, como buenos amigos.',
    'Los Pokémon están en todas partes: en la hierba alta, en las cuevas, en el mar...',
    'Yo me dedico a investigar cómo viven y cómo se relacionan con las personas.'
  ];

  /** Texto mostrado cuando el profesor entrega la Pokédex. */
  lineaPokedex = 'Toma, esta es una Pokédex. Registrará automáticamente los datos de los Pokémon que veas o captures.';

  /** Texto mostrado durante la selección de Pokébolas desde el maletín. */
  lineaPedirInicial = 'Ahora, dentro de este maletín tengo tres Pokébolas. ¡Elige al Pokémon que te acompañará!';

  /** Última línea de diálogo antes de iniciar la transición de salida hacia `/game`. */
  lineaDespedida = '¡Muchas aventuras pokemón te están esperando! ¡Mucha suerte!';

  /** Catálogo de Pokémon iniciales (Treecko, Torchic y Mudkip) disponibles para elegir. */
  iniciales: InicialOpcion[] = [
    {
      id: 252,
      nombre: 'Treecko',
      sprite: '/assets/sprites/normal/252.png',
      pokeball: '/assets/images/pokeicon.png'
    },
    {
      id: 255,
      nombre: 'Torchic',
      sprite: '/assets/sprites/normal/255.png',
      pokeball: '/assets/images/pokeicon.png'
    },
    {
      id: 258,
      nombre: 'Mudkip',
      sprite: '/assets/sprites/normal/258.png',
      pokeball: '/assets/images/pokeicon.png'
    }
  ];

  /**
   * Estado actual de la secuencia de introducción:
   * `'previo' -> 'pokedex' -> 'seleccion' -> 'confirmado' -> 'despedida'`.
   */
  fase = signal<'previo' | 'pokedex' | 'seleccion' | 'confirmado' | 'despedida'>('previo');

  /** Índice de la línea actual dentro de `lineasAntesPokedex` (cuando `fase` es `'previo'`). */
  indice = signal(0);

  /** Fragmento o línea completa de texto que se renderiza en el cuadro de diálogo. */
  textoMostrado = signal('');

  /** Indica si actualmente está corriendo el efecto de escritura letra por letra. */
  escribiendo = signal(false);

  /** Controla si se muestra el sprite del Profesor Abedul (se oculta si falla la carga). */
  mostrarImagen = signal(true);

  /** Controla si se muestra el sprite del Pokémon en escena. */
  mostrarPokemon = signal(true);

  /** Controla si se muestra la imagen del maletín en la fase de selección. */
  mostrarMaleta = signal(true);

  /** Activa la animación de desvanecimiento de salida antes de navegar a `/game`. */
  saliendo = signal(false);

  /** Almacena el Pokémon inicial elegido por el jugador, o `null` si aún no se ha elegido. */
  inicialSeleccionado = signal<InicialOpcion | null>(null);

  /** Ruta del sprite del Pokémon mostrado actualmente (inicialmente un sprite base, luego el elegido). */
  pokemonActualSprite = signal('/assets/sprites/normal/001.png');

  /** Identificador del intervalo usado para el efecto de máquina de escribir. */
  private temporizador: ReturnType<typeof setInterval> | undefined;

  /** Identificador del temporizador que ejecuta la navegación tras la animación de salida. */
  private salida: ReturnType<typeof setTimeout> | undefined;

  /**
   * Inicializa el componente comenzando a escribir la primera línea introductoria.
   */
  ngOnInit(): void {
    this.escribirTexto(this.lineasAntesPokedex[0]);
  }

  /**
   * Limpia intervalos y temporizadores activos al destruir el componente para evitar fugas de memoria.
   */
  ngOnDestroy(): void {
    clearInterval(this.temporizador);
    clearTimeout(this.salida);
  }

  /**
   * Escucha pulsaciones de teclas a nivel de `window`.
   * Permite avanzar con `x` o `Espacio` (ignorando repetición al mantener presionada),
   * excepto durante la fase `'seleccion'`, donde la elección se realiza en una Pokébola.
   *
   * @param e Evento de teclado capturado.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionar(e: KeyboardEvent): void {
    if ((e.key === 'x' || e.key === ' ') && !e.repeat) {
      e.preventDefault();
      // En la fase de selección de pokébolas, el usuario elige haciendo clic en una
      if (this.fase() !== 'seleccion') {
        this.avanzar();
      }
    }
  }

  /**
   * Registra el Pokémon inicial seleccionado, actualiza su sprite en escena,
   * cambia a la fase `'confirmado'` y muestra el mensaje de confirmación.
   *
   * @param opcion Opción de Pokémon inicial elegida por el jugador.
   * @param event Evento de clic para detener la propagación hacia el contenedor padre.
   */
  seleccionarInicial(opcion: InicialOpcion, event: MouseEvent): void {
    event.stopPropagation();
    if (this.fase() !== 'seleccion') return;

    this.inicialSeleccionado.set(opcion);
    this.pokemonActualSprite.set(opcion.sprite);
    this.mostrarPokemon.set(true);
    this.fase.set('confirmado');
    this.escribirTexto(`¡Has elegido a ${opcion.nombre}! Es un gran compañero.`);
  }

  /**
   * Avanza el flujo de la introducción.
   * Si el texto aún se está escribiendo, lo completa de inmediato;
   * de lo contrario, pasa a la siguiente línea o cambia de `fase` hasta navegar a `/game`.
   */
  avanzar(): void {
    if (this.saliendo()) return;

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
        this.indice.update(i => i + 1);
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
      // Espera a que el jugador seleccione una de las 3 pokébolas
      return;
    }

    if (faseActual === 'confirmado') {
      this.fase.set('despedida');
      this.escribirTexto(this.lineaDespedida);
      return;
    }

    if (faseActual === 'despedida') {
      this.saliendo.set(true);
      this.salida = setTimeout(() => this.router.navigate(['/game']), 600);
    }
  }

  /**
   * Obtiene el texto completo correspondiente a la `fase` activa.
   * Útil para autocompletar el diálogo si el usuario interrumpe la animación de escritura.
   *
   * @returns La cadena esperada para la fase actual o `null` si no aplica.
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
        const sel = this.inicialSeleccionado();
        return sel ? `¡Has elegido a ${sel.nombre}! Es un gran compañero.` : '';
      }
      case 'despedida':
        return this.lineaDespedida;
    }
  }

  /**
   * Inicia el efecto progresivo de máquina de escribir para una línea de texto.
   * Cancela cualquier intervalo previo antes de comenzar.
   *
   * @param linea Texto completo que se irá revelando carácter por carácter.
   */
  private escribirTexto(linea: string): void {
    clearInterval(this.temporizador);
    this.textoMostrado.set('');
    this.escribiendo.set(true);
    let i = 0;
    this.temporizador = setInterval(() => {
      i++;
      this.textoMostrado.set(linea.slice(0, i));
      if (i >= linea.length) {
        clearInterval(this.temporizador);
        this.escribiendo.set(false);
      }
    }, 25);
  }
}
