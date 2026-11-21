import { Component, ElementRef, HostListener, NgZone, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { Mapaservice } from '../../services/mapaservice';
import { Mapadatosmodel } from '../../models/mapadatosmodel';
import { Direccionmodel, Dialogomodel, Jugadormodel, Npcmodel } from '../../models/npcmodel';
import { EncounterAction, Wildpokemonmodel } from '../../models/wildpokemonmodel';
import { BagItemModel } from '../../models/bagmodel';

/**
 * Componente principal del mapa jugable estilo Game Boy Advance.
 * Gestiona el renderizado del mapa en `<canvas>` (`240x160`), el desplazamiento por tiles,
 * las colisiones, la cámara, los NPCs, los diálogos y los sucesos del entorno
 * (encuentros/capturas, curación, batallas y otros eventos).
 */
@Component({
  selector: 'app-mapcomponent',
  standalone: false,
  styleUrl: './mapcomponent.css',
  templateUrl: './mapcomponent.html',
})
export class Mapcomponent implements OnInit, OnDestroy {
  /** Referencia al elemento `<canvas>` donde se dibuja el escenario y las entidades. */
  @ViewChild('lienzo', { static: true }) lienzo!: ElementRef<HTMLCanvasElement>;

  /** Servicio de mapa encargado de cargar los datos y validar celdas caminables. */
  private mapaservice = inject(Mapaservice);

  /** Zona de Angular para mantener el bucle gráfico fuera de la detección de cambios en cada cuadro. */
  private zone = inject(NgZone);

  /** Ancho lógico del lienzo estilo GBA en píxeles (escalado posteriormente por CSS). */
  private readonly ANCHO = 240;

  /** Alto lógico del lienzo estilo GBA en píxeles (escalado posteriormente por CSS). */
  private readonly ALTO = 160;

  /** Ruta del spritesheet del personaje principal. */
  private readonly RUTA_SPRITE_JUGADOR = '/assets/sprites/normal/men.png';

  /** Contexto 2D del `<canvas>` para renderizar fondo, NPCs y jugador. */
  private ctx!: CanvasRenderingContext2D;

  /** Datos cargados del mapa (dimensiones, tamaño de tile y capas de colisión/tiles). */
  private datos!: Mapadatosmodel;

  /** Imagen completa del fondo del mapa. */
  private fondo = new Image();

  /** Hoja de sprites del jugador cargada desde `RUTA_SPRITE_JUGADOR`. */
  private spriteJugador = new Image();

  /** Mapa de imágenes de sprites por `id` de NPC. */
  private spritesNpc = new Map<string, HTMLImageElement>();

  /** Identificador del cuadro de animación activo (`requestAnimationFrame`). */
  private idAnimacion = 0;

  /** Marca de tiempo del cuadro anterior para calcular el delta `dt`. */
  private ultimoTiempo = 0;

  /** Direcciones presionadas actualmente; la última agregada define la orientación. */
  private teclas: Direccionmodel[] = [];

  /** Temporizador para el efecto de máquina de escribir en diálogos. */
  private temporizador: ReturnType<typeof setInterval> | undefined;

  /** Pokémon salvaje activo en pantalla; cuando no es `null`, pausa el movimiento en el mapa. */
  pokemonSalvaje: Wildpokemonmodel | null = null;

  /** Duración en ms de cada paso al caminar normal. */
  private readonly DURACION_PASO_NORMAL = 250;

  /** Duración en ms de cada paso al mantener presionada la tecla Z. */
  private readonly DURACION_PASO_CORRER = 120;

  /** Indica si el jugador está corriendo con la tecla Z. */
  private corriendo = false;

  /** Controla si la mochila está abierta en pantalla. */
  mochilaAbierta = false;

  /** Estado actual del jugador en la cuadrícula del mapa. */
  jugador: Jugadormodel = {
    col: 128,
    fila: 167,
    direccion: 'abajo',
    moviendo: false,
    progreso: 0,
    paso: 0,
  };

  /** NPCs presentes en el mapa con los que el jugador puede interactuar. */
  npcs: Npcmodel[] = [];

  /** Diálogo activo en pantalla, o `null` cuando no hay conversación en curso. */
  dialogoActivo: Dialogomodel | null = null;

  /** Texto revelado hasta el momento en la línea de diálogo activa. */
  textoMostrado = '';

  /** Índice de la línea actual dentro de `dialogoActivo.lineas`. */
  private indiceLinea = 0;

  /**
   * Inicializa el contexto del lienzo, carga la hoja de sprites del jugador
   * y los NPCs, y arranca el bucle del juego una vez cargados el mapa y su imagen de fondo.
   */
  ngOnInit(): void {
    this.ctx = this.lienzo.nativeElement.getContext('2d')!;
    this.cargarSprites();

    this.mapaservice.cargarMapa('/assets/maps/mapapokemon.tmj').subscribe((datos) => {
      this.datos = datos;
      this.fondo.onload = () => {
        this.zone.runOutsideAngular(() => {
          this.idAnimacion = requestAnimationFrame(this.bucle);
        });
      };
      this.fondo.src = '/assets/maps/mapapokemonimage.png';
    });
  }

  /**
   * Cancela el bucle de animación y limpia temporizadores pendientes al destruir el componente.
   */
  ngOnDestroy(): void {
    cancelAnimationFrame(this.idAnimacion);
    clearInterval(this.temporizador);
  }

  /**
   * Precarga el spritesheet del jugador y las imágenes asociadas a cada NPC del mapa.
   */
  private cargarSprites(): void {
    this.spriteJugador.src = this.RUTA_SPRITE_JUGADOR;
    for (const npc of this.npcs) {
      const img = new Image();
      img.src = npc.sprite;
      this.spritesNpc.set(npc.id, img);
    }
  }

  /**
   * Captura teclas direccionales para mover al jugador y teclas de acción para interactuar.
   *
   * @param e Evento de teclado a nivel de ventana.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionar(e: KeyboardEvent): void {
    const tecla = e.key.toLowerCase();

    const dir = this.teclaADireccion(e.key);
    if (dir) {
      e.preventDefault();
      if (!this.teclas.includes(dir)) this.teclas.push(dir);
      return;
    }

    if (tecla === 'z') {
      e.preventDefault();
      this.corriendo = true;
      return;
    }

    if (e.key === 'Enter' && !e.repeat) {
      e.preventDefault();
      this.alternarMochila();
      return;
    }

    if (tecla === 'x' && !e.repeat) {
      e.preventDefault();
      this.aceptarConTeclaX();
      return;
    }

    if (e.key === ' ') {
      e.preventDefault();
      this.interactuar();
      return;
    }

    if (tecla === 'e' && !e.repeat && !this.pokemonSalvaje && !this.dialogoActivo && !this.mochilaAbierta) {
      e.preventDefault();
      this.abrirEncuentroPrueba();
    }
  }

  /**
   * Retira una dirección de la cola de teclas activas al soltar la tecla correspondiente.
   *
   * @param e Evento de liberación de tecla.
   */
  @HostListener('window:keyup', ['$event'])
  alSoltar(e: KeyboardEvent): void {
    const tecla = e.key.toLowerCase();

    if (tecla === 'z') {
      this.corriendo = false;
    }

    const dir = this.teclaADireccion(e.key);
    if (dir) this.teclas = this.teclas.filter((t) => t !== dir);
  }

  /**
   * Convierte una tecla de dirección (`Flechas` o `WASD`) en un valor de `Direccionmodel`.
   *
   * @param tecla Nombre de la tecla presionada (`event.key`).
   * @returns Dirección correspondiente o `null` si no aplica.
   */
  private teclaADireccion(tecla: string): Direccionmodel | null {
    switch (tecla.toLowerCase()) {
      case 'arrowup':
        return 'arriba';
      case 'arrowdown':
        return 'abajo';
      case 'arrowleft':
        return 'izquierda';
      case 'arrowright':
        return 'derecha';
      default:
        return null;
    }
  }

  /**
   * Bucle principal de actualización y dibujado del mapa.
   *
   * @param t Marca de tiempo actual de `requestAnimationFrame`.
   */
  private bucle = (t: number): void => {
    const dt = Math.min(t - this.ultimoTiempo, 50);
    this.ultimoTiempo = t;
    this.actualizar(dt);
    this.dibujar();
    this.idAnimacion = requestAnimationFrame(this.bucle);
  };

  /**
   * Calcula el desplazamiento unitario en columnas (`dc`) y filas (`df`) para una dirección.
   *
   * @param dir Dirección cardinal.
   * @returns Desplazamiento en `dc` y `df`.
   */
  private vector(dir: Direccionmodel): { dc: number; df: number } {
    switch (dir) {
      case 'arriba':
        return { dc: 0, df: -1 };
      case 'abajo':
        return { dc: 0, df: 1 };
      case 'izquierda':
        return { dc: -1, df: 0 };
      default:
        return { dc: 1, df: 0 };
    }
  }

  /**
   * Devuelve el NPC situado en la celda `(col, fila)`, si existe alguno.
   *
   * @param col Columna del tile.
   * @param fila Fila del tile.
   * @returns El NPC encontrado o `undefined`.
   */
  private npcEn(col: number, fila: number): Npcmodel | undefined {
    return this.npcs.find((n) => n.col === col && n.fila === fila);
  }

  /**
   * Actualiza el progreso de movimiento del jugador, evalúa sucesos al completar un paso
   * e inicia el siguiente desplazamiento si hay dirección presionada y celda libre.
   *
   * @param dt Tiempo transcurrido desde el cuadro anterior en milisegundos.
   */
  private actualizar(dt: number): void {
    const j = this.jugador;

    if (this.dialogoActivo || this.pokemonSalvaje || this.mochilaAbierta) {
      return;
    }

    if (j.moviendo) {
      const duracionActual = this.corriendo ? this.DURACION_PASO_CORRER : this.DURACION_PASO_NORMAL;
      j.progreso += dt / duracionActual;
      if (j.progreso >= 1) {
        const v = this.vector(j.direccion);
        j.col += v.dc;
        j.fila += v.df;
        j.progreso = 0;
        j.moviendo = false;
        j.paso = (j.paso + 1) % 2;
        this.evaluarSucesosTile(j.col, j.fila);
      }
      return;
    }

    if (this.teclas.length > 0) {
      j.direccion = this.teclas[this.teclas.length - 1];
      const v = this.vector(j.direccion);
      const c = j.col + v.dc;
      const f = j.fila + v.df;
      if (this.mapaservice.esCaminable(this.datos, c, f) && !this.npcEn(c, f)) {
        j.moviendo = true;
        j.progreso = 0;
      }
    }
  }

  /**
   * Abre un encuentro salvaje de prueba para verificar la UI de `Wildencountercomponent`.
   */
  abrirEncuentroPrueba(): void {
    this.teclas = [];
    this.pokemonSalvaje = {
      id: 263,
      nombre: 'Zigzagoon',
      nivel: 3,
    };
  }

  /**
   * Maneja la opción elegida dentro de `Wildencountercomponent`.
   *
   * @param accion Acción seleccionada (`battle`, `bag` o `run`).
   */
  alElegirAccionEncuentro(accion: EncounterAction): void {
    if (accion === 'bag') {
      this.mochilaAbierta = true;
      return;
    }

    if (accion === 'run') {
      this.mochilaAbierta = false;
      this.pokemonSalvaje = null;
      return;
    }

    // Por ahora cerramos el encuentro al elegir 'battle' hasta conectar la lógica de combate
    this.mochilaAbierta = false;
    this.pokemonSalvaje = null;
  }

  /**
   * Cierra la mochila y regresa al estado anterior (encuentro salvaje o mapa).
   */
  alCerrarMochila(): void {
    this.mochilaAbierta = false;
  }

  /**
   * Reacciona al seleccionar un objeto dentro de la mochila (sin lógica de uso aún).
   *
   * @param item Objeto seleccionado en `Bagcomponent`.
   */
  alSeleccionarObjetoMochila(item: BagItemModel): void {
    void item;
  }

  /**
   * Punto de extensión para gestionar eventos al pisar una celda del mapa
   * (por ejemplo: encuentros/capturas, curación, batallas o transiciones de zona).
   *
   * @param col Columna alcanzada por el jugador.
   * @param fila Fila alcanzada por el jugador.
   */
  private evaluarSucesosTile(col: number, fila: number): void {
    void col;
    void fila;
  }

  /**
   * Dibuja el segmento visible del mapa centrado en el jugador y renderiza NPCs y jugador
   * ordenados por coordenada vertical (`y`).
   */
  private dibujar(): void {
    const ctx = this.ctx;
    const T = this.datos.tamanoTile;
    const j = this.jugador;
    const v = this.vector(j.direccion);

    const px = (j.col + (j.moviendo ? v.dc * j.progreso : 0)) * T;
    const py = (j.fila + (j.moviendo ? v.df * j.progreso : 0)) * T;

    const camX = Math.round(Math.max(0, Math.min(px + T / 2 - this.ANCHO / 2, this.fondo.width - this.ANCHO)));
    const camY = Math.round(Math.max(0, Math.min(py + T / 2 - this.ALTO / 2, this.fondo.height - this.ALTO)));

    ctx.imageSmoothingEnabled = false;
    ctx.clearRect(0, 0, this.ANCHO, this.ALTO);
    ctx.drawImage(this.fondo, camX, camY, this.ANCHO, this.ALTO, 0, 0, this.ANCHO, this.ALTO);

    const entidades = [
      ...this.npcs.map((n) => ({ y: n.fila * T, dibujar: () => this.dibujarNpc(n, camX, camY) })),
      { y: py, dibujar: () => this.dibujarJugador(px, py, camX, camY) },
    ].sort((a, b) => a.y - b.y);
    entidades.forEach((e) => e.dibujar());
  }

  /**
   * Dibuja al jugador desde su hoja de 4x4 (`men.png`):
   * - Filas: `0` abajo, `1` izquierda, `2` derecha, `3` arriba.
   * - Columnas: `0` quieto, `1` y `3` alternando al caminar.
   *
   * @param px Posición X del jugador en píxeles dentro del mapa.
   * @param py Posición Y del jugador en píxeles dentro del mapa.
   * @param camX Desplazamiento X de la cámara.
   * @param camY Desplazamiento Y de la cámara.
   */
  private dibujarJugador(px: number, py: number, camX: number, camY: number): void {
    const img = this.spriteJugador;
    if (!img.complete || img.naturalWidth === 0) return;

    const cols = 4;
    const filas = 4;
    const sw = img.naturalWidth / cols;
    const sh = img.naturalHeight / filas;

    const filaMap: Record<Direccionmodel, number> = {
      abajo: 0,
      izquierda: 1,
      derecha: 2,
      arriba: 3,
    };

    const fila = filaMap[this.jugador.direccion];
    const col = this.jugador.moviendo ? (this.jugador.paso === 0 ? 1 : 3) : 0;

    const sx = col * sw;
    const sy = fila * sh;

    const destW = 28;
    const destH = 24;
    const destX = Math.round(px - camX);
    const destY = Math.round(py - camY - (destH - 16));

    this.ctx.drawImage(img, sx, sy, sw, sh, destX, destY, destW, destH);
  }

  /**
   * Dibuja un NPC en su posición de tile ajustada por la cámara.
   *
   * @param n Modelo del NPC.
   * @param camX Desplazamiento X de la cámara.
   * @param camY Desplazamiento Y de la cámara.
   */
  private dibujarNpc(n: Npcmodel, camX: number, camY: number): void {
    const img = this.spritesNpc.get(n.id);
    if (img) this.dibujarSprite(img, n.direccion, false, 0, n.col * 16 - camX, n.fila * 16 - camY);
  }

  /**
   * Recorta y dibuja un cuadro de sprite (`16x16`) según la dirección y el estado de caminata.
   *
   * @param img Hoja de sprites a dibujar.
   * @param dir Dirección hacia la que mira o avanza la entidad.
   * @param moviendo Indica si está en medio de un desplazamiento.
   * @param paso Alterna entre `0` y `1` para animar los pasos.
   * @param x Coordenada horizontal en el lienzo.
   * @param y Coordenada vertical base del tile en el lienzo.
   */
  private dibujarSprite(
    img: HTMLImageElement,
    dir: Direccionmodel,
    moviendo: boolean,
    paso: number,
    x: number,
    y: number
  ): void {
    if (!img.complete || img.naturalWidth === 0) return;
    let cuadro = 0;
    if (dir === 'abajo') cuadro = moviendo ? 3 + paso : 0;
    if (dir === 'arriba') cuadro = moviendo ? 5 + paso : 1;
    if (dir === 'izquierda' || dir === 'derecha') cuadro = moviendo ? 7 + paso : 2;

    const ctx = this.ctx;
    const destY = y - 16;
    if (dir === 'derecha') {
      ctx.save();
      ctx.translate(x + 16, destY);
      ctx.scale(-1, 1);
      ctx.drawImage(img, cuadro * 16, 0, 16, 16, 0, 0, 16, 16);
      ctx.restore();
    } else {
      ctx.drawImage(img, cuadro * 16, 0, 16, 16, x, destY, 16, 16);
    }
  }

  /**
   * Avanza el diálogo activo o inicia conversación si hay un NPC frente al jugador.
   */
  private interactuar(): void {
    if (this.dialogoActivo) {
      this.avanzarDialogo();
      return;
    }
    const j = this.jugador;
    if (j.moviendo) return;
    const v = this.vector(j.direccion);
    const npc = this.npcEn(j.col + v.dc, j.fila + v.df);
    if (npc) {
      npc.direccion = this.opuesta(j.direccion);
      this.dialogoActivo = { nombre: npc.nombre, lineas: npc.dialogo };
      this.indiceLinea = 0;
      this.escribir();
    }
  }

  /**
   * Retorna la dirección opuesta a la recibida.
   *
   * @param d Dirección original.
   * @returns Dirección opuesta.
   */
  private opuesta(d: Direccionmodel): Direccionmodel {
    return ({ arriba: 'abajo', abajo: 'arriba', izquierda: 'derecha', derecha: 'izquierda' } as const)[d];
  }

  /**
   * Escribe progresivamente la línea actual del diálogo activo.
   */
  private escribir(): void {
    clearInterval(this.temporizador);
    const linea = this.dialogoActivo!.lineas[this.indiceLinea];
    this.textoMostrado = '';
    let i = 0;
    this.temporizador = setInterval(() => {
      this.textoMostrado = linea.slice(0, ++i);
      if (i >= linea.length) clearInterval(this.temporizador);
    }, 30);
  }

  /**
   * Completa la línea en curso, pasa a la siguiente o cierra el diálogo al terminar.
   */
  private avanzarDialogo(): void {
    const d = this.dialogoActivo!;
    const linea = d.lineas[this.indiceLinea];
    if (this.textoMostrado.length < linea.length) {
      clearInterval(this.temporizador);
      this.textoMostrado = linea;
      return;
    }
    if (this.indiceLinea < d.lineas.length - 1) {
      this.indiceLinea++;
      this.escribir();
    } else {
      this.dialogoActivo = null;
    }
  }

  /**
   * Abre o cierra la mochila con Enter (si no hay un encuentro salvaje ni diálogo).
   */
  private alternarMochila(): void {
    if (this.mochilaAbierta) {
      this.mochilaAbierta = false;
      return;
    }
    if (!this.pokemonSalvaje && !this.dialogoActivo) {
      this.teclas = [];
      this.mochilaAbierta = true;
    }
  }

  /**
   * Simula una confirmación ("aceptar") al presionar X:
   * avanza diálogos o hace clic en el botón activo/enfocado.
   */
  private aceptarConTeclaX(): void {
    if (this.dialogoActivo) {
      this.avanzarDialogo();
      return;
    }

    if (this.mochilaAbierta) {
      const filaMochila = document.querySelector<HTMLButtonElement>(
        '.mochila-overlay .fila-mochila.fila-activa:not([disabled])'
      );
      filaMochila?.click();
      return;
    }

    if (this.pokemonSalvaje && !this.mochilaAbierta) {
      const opcionActiva = document.querySelector<HTMLButtonElement>(
        '.encuentro-overlay .opcion-btn.seleccionada:not([disabled])'
      );
      opcionActiva?.click();
      return;
    }

    const elementoActivo = document.activeElement as HTMLElement | null;
    if (
      elementoActivo &&
      elementoActivo.tagName === 'BUTTON' &&
      !(elementoActivo as HTMLButtonElement).disabled
    ) {
      elementoActivo.click();
      return;
    }

    const botonVisible = document.querySelector<HTMLButtonElement>(
      '.mochila-overlay button:not([disabled]), .encuentro-overlay button:not([disabled])'
    );
    botonVisible?.click();
  }
}
