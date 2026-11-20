import { Component, ElementRef, HostListener, NgZone, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { Mapaservice } from '../../services/mapaservice';
import { Mapadatosmodel } from '../../models/mapadatosmodel';
import { Direccionmodel, Dialogomodel, Jugadormodel, Npcmodel } from '../../models/npcmodel';

/**
 * Componente principal del mapa jugable estilo Game Boy Advance.
 * Renderiza el escenario de Pueblo Raíz en un `<canvas>` de `240x160`, gestiona
 * el movimiento por cuadrícula (tiles), las colisiones, la cámara, los NPCs
 * y el sistema de diálogos interactivos.
 */
@Component({
  selector: 'app-mapcomponent',
  standalone: false,
  styleUrl: './mapcomponent.css',
  templateUrl: './mapcomponent.html',
})
export class Mapcomponent implements OnInit, OnDestroy {
  /** Referencia al elemento `<canvas>` donde se dibuja el mapa y las entidades. */
  @ViewChild('lienzo', { static: true }) lienzo!: ElementRef<HTMLCanvasElement>;

  /** Servicio encargado de cargar los datos del mapa y verificar si una celda es caminable. */
  private mapaservice = inject(Mapaservice);

  /** Zona de Angular usada para ejecutar el bucle de animación fuera de la detección de cambios en cada cuadro. */
  private zone = inject(NgZone);

  /** Ancho lógico del lienzo estilo GBA en píxeles (escalado luego mediante CSS). */
  private readonly ANCHO = 240;

  /** Alto lógico del lienzo estilo GBA en píxeles (escalado luego mediante CSS). */
  private readonly ALTO = 160;

  /** Duración en milisegundos que tarda el jugador en desplazarse de un tile al siguiente. */
  private readonly DURACION_PASO = 250;

  /** Contexto 2D del `<canvas>` utilizado para dibujar fondo, sprites y entidades. */
  private ctx!: CanvasRenderingContext2D;

  /** Estructura cargada del mapa con dimensiones, capas y datos de colisión/tiles. */
  private datos!: Mapadatosmodel;

  /** Imagen de fondo completa del mapa renderizado. */
  private fondo = new Image();

  /** Hoja de sprites correspondiente al jugador. */
  private spriteJugador = new Image();

  /** Diccionario de imágenes de sprites indexadas por el `id` de cada NPC. */
  private spritesNpc = new Map<string, HTMLImageElement>();

  /** Identificador devuelto por `requestAnimationFrame` para cancelar el bucle al destruir el componente. */
  private idAnimacion = 0;

  /** Marca de tiempo del cuadro anterior utilizada para calcular el delta `dt`. */
  private ultimoTiempo = 0;

  /** Cola de direcciones actualmente presionadas por el jugador (la última tiene prioridad). */
  private teclas: Direccionmodel[] = [];

  /** Identificador del intervalo usado para el efecto progresivo de escritura en diálogos. */
  private temporizador: ReturnType<typeof setInterval> | undefined;

  /** Estado actual del jugador en el mapa (posición en tiles, dirección, animación y progreso de paso). */
  jugador: Jugadormodel = { col: 128, fila: 167, direccion: 'abajo', moviendo: false, progreso: 0, paso: 0 };

  /** Lista de NPCs presentes en el mapa con su posición, sprite y líneas de diálogo. */
  npcs: Npcmodel[] = [
    {
      id: 'abedul', nombre: 'Prof. Abedul', col: 124, fila: 167, direccion: 'abajo',
      sprite: '/assets/sprites/normal/002.png',
      dialogo: ['¡Hola! Bienvenido al mundo Pokémon.', 'Pasa por mi laboratorio cuando estés listo.']
    }
  ];

  /** Diálogo actualmente en curso al hablar con un NPC, o `null` si no hay conversación activa. */
  dialogoActivo: Dialogomodel | null = null;

  /** Texto revelado hasta el momento en la línea de diálogo activa. */
  textoMostrado = '';

  /** Índice de la línea actual dentro de `dialogoActivo.lineas`. */
  private indiceLinea = 0;

  /**
   * Inicializa el contexto del lienzo, precarga los sprites del jugador y los NPCs,
   * y arranca el bucle de animación una vez cargados los datos y la imagen del mapa.
   */
  ngOnInit(): void {
    this.ctx = this.lienzo.nativeElement.getContext('2d')!;
    this.spriteJugador.src = '/assets/sprites/normal/001.png';
    for (const npc of this.npcs) {
      const img = new Image();
      img.src = npc.sprite;
      this.spritesNpc.set(npc.id, img);
    }

    this.mapaservice.cargarMapa('/assets/maps/mapapokemon.tmj').subscribe(datos => {
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
   * Detiene el bucle de `requestAnimationFrame` y limpia cualquier temporizador de texto activo.
   */
  ngOnDestroy(): void {
    cancelAnimationFrame(this.idAnimacion);
    clearInterval(this.temporizador);
  }

  /**
   * Registra teclas direccionales presionadas o activa la interacción con NPCs/diálogos.
   *
   * @param e Evento de teclado capturado a nivel de ventana.
   */
  @HostListener('window:keydown', ['$event'])
  alPresionar(e: KeyboardEvent): void {
    const dir = this.teclaADireccion(e.key);
    if (dir) {
      e.preventDefault();
      if (!this.teclas.includes(dir)) this.teclas.push(dir);
      return;
    }
    if (e.key === 'Enter' || e.key === ' ' || e.key.toLowerCase() === 'z') {
      e.preventDefault();
      this.interactuar();
    }
  }

  /**
   * Elimina de la cola activa la dirección asociada a la tecla que se ha soltado.
   *
   * @param e Evento de teclado al liberar una tecla.
   */
  @HostListener('window:keyup', ['$event'])
  alSoltar(e: KeyboardEvent): void {
    const dir = this.teclaADireccion(e.key);
    if (dir) this.teclas = this.teclas.filter(t => t !== dir);
  }

  /**
   * Traduce una tecla física (`Flechas` o `WASD`) a una dirección del modelo del juego.
   *
   * @param tecla Valor de `event.key`.
   * @returns La dirección correspondiente o `null` si la tecla no es direccional.
   */
  private teclaADireccion(tecla: string): Direccionmodel | null {
    switch (tecla.toLowerCase()) {
      case 'arrowup': case 'w': return 'arriba';
      case 'arrowdown': case 's': return 'abajo';
      case 'arrowleft': case 'a': return 'izquierda';
      case 'arrowright': case 'd': return 'derecha';
      default: return null;
    }
  }

  /**
   * Ejecuta un ciclo completo de actualización física/lógica y renderizado en `<canvas>`.
   * Limita `dt` a `50 ms` para evitar saltos bruscos si la pestaña pierde foco.
   *
   * @param t Marca de tiempo actual proporcionada por `requestAnimationFrame`.
   */
  private bucle = (t: number): void => {
    const dt = Math.min(t - this.ultimoTiempo, 50);
    this.ultimoTiempo = t;
    this.actualizar(dt);
    this.dibujar();
    this.idAnimacion = requestAnimationFrame(this.bucle);
  };

  /**
   * Convierte una dirección cardinal en un desplazamiento unitario de columnas (`dc`) y filas (`df`).
   *
   * @param dir Dirección de movimiento o mirada.
   * @returns Objeto con los incrementos `dc` y `df`.
   */
  private vector(dir: Direccionmodel): { dc: number; df: number } {
    switch (dir) {
      case 'arriba': return { dc: 0, df: -1 };
      case 'abajo': return { dc: 0, df: 1 };
      case 'izquierda': return { dc: -1, df: 0 };
      default: return { dc: 1, df: 0 };
    }
  }

  /**
   * Busca si existe un NPC ubicado exactamente en la columna y fila indicadas.
   *
   * @param col Columna del tile a consultar.
   * @param fila Fila del tile a consultar.
   * @returns El `Npcmodel` encontrado en esa celda o `undefined`.
   */
  private npcEn(col: number, fila: number): Npcmodel | undefined {
    return this.npcs.find(n => n.col === col && n.fila === fila);
  }

  /**
   * Avanza el progreso de un paso en curso y evalúa si puede iniciar un nuevo movimiento
   * hacia la siguiente celda caminable y libre de NPCs.
   *
   * @param dt Tiempo transcurrido desde el último cuadro en milisegundos.
   */
  private actualizar(dt: number): void {
    const j = this.jugador;

    if (j.moviendo) {
      j.progreso += dt / this.DURACION_PASO;
      if (j.progreso >= 1) {
        const v = this.vector(j.direccion);
        j.col += v.dc;
        j.fila += v.df;
        j.progreso = 0;
        j.moviendo = false;
        j.paso = (j.paso + 1) % 2;
      }
    }

    if (!j.moviendo && !this.dialogoActivo && this.teclas.length > 0) {
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
   * Calcula la posición interpolada del jugador, centra la cámara dentro de los límites del mapa
   * y dibuja el fondo junto con los NPCs y el jugador ordenados por profundidad (`y`).
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
      ...this.npcs.map(n => ({ y: n.fila * T, dibujar: () => this.dibujarNpc(n, camX, camY) })),
      { y: py, dibujar: () => this.dibujarJugador(px, py, camX, camY) }
    ].sort((a, b) => a.y - b.y);
    entidades.forEach(e => e.dibujar());
  }

  /**
   * Dibuja al jugador en coordenadas relativas a la cámara según su estado actual de movimiento.
   *
   * @param px Posición horizontal del jugador en píxeles dentro del mapa.
   * @param py Posición vertical del jugador en píxeles dentro del mapa.
   * @param camX Desplazamiento horizontal actual de la cámara.
   * @param camY Desplazamiento vertical actual de la cámara.
   */
  private dibujarJugador(px: number, py: number, camX: number, camY: number): void {
    const j = this.jugador;
    this.dibujarSprite(this.spriteJugador, j.direccion, j.moviendo, j.paso, px - camX, py - camY);
  }

  /**
   * Dibuja un NPC estático en su posición de tile ajustada por la cámara.
   *
   * @param n Datos del NPC a renderizar.
   * @param camX Desplazamiento horizontal actual de la cámara.
   * @param camY Desplazamiento vertical actual de la cámara.
   */
  private dibujarNpc(n: Npcmodel, camX: number, camY: number): void {
    const img = this.spritesNpc.get(n.id);
    if (img) this.dibujarSprite(img, n.direccion, false, 0, n.col * 16 - camX, n.fila * 16 - camY);
  }

  /**
   * Dibuja un cuadro de una hoja de sprites de `16x32` (9 cuadros en total, estilo Rubí/Esmeralda):
   * - `0`: quieto abajo, `1`: quieto arriba, `2`: quieto izquierda.
   * - `3-4`: caminar abajo, `5-6`: caminar arriba, `7-8`: caminar izquierda.
   * - Para `'derecha'`, se refleja horizontalmente el sprite de izquierda.
   *
   * @param img Imagen de la hoja de sprites (`HTMLImageElement`).
   * @param dir Dirección hacia la que mira o camina la entidad.
   * @param moviendo Indica si la entidad está desplazándose para alternar cuadros de caminar.
   * @param paso Índice alternante (`0` o `1`) del ciclo de caminar.
   * @param x Coordenada horizontal de destino en el lienzo.
   * @param y Coordenada vertical base (pies en el tile) en el lienzo.
   */
  private dibujarSprite(img: HTMLImageElement, dir: Direccionmodel, moviendo: boolean, paso: number, x: number, y: number): void {
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
      ctx.drawImage(img, cuadro * 16, 0, 16, 32, 0, 0, 16, 32);
      ctx.restore();
    } else {
      ctx.drawImage(img, cuadro * 16, 0, 16, 32, x, destY, 16, 32);
    }
  }

  /**
   * Continúa el diálogo activo si existe; de lo contrario, comprueba si hay un NPC frente
   * al jugador para orientarlo hacia él e iniciar su conversación.
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
   * Devuelve la dirección cardinal opuesta para que un NPC mire de frente al jugador.
   *
   * @param d Dirección actual del jugador.
   * @returns Dirección opuesta.
   */
  private opuesta(d: Direccionmodel): Direccionmodel {
    return ({ arriba: 'abajo', abajo: 'arriba', izquierda: 'derecha', derecha: 'izquierda' } as const)[d];
  }

  /**
   * Inicia el efecto letra por letra para la línea actual del diálogo activo.
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
   * Completa la línea actual si aún se está escribiendo, pasa a la siguiente línea
   * o cierra el cuadro de diálogo cuando se han leído todas.
   */
  private avanzarDialogo(): void {
    const d = this.dialogoActivo!;
    const linea = d.lineas[this.indiceLinea];
    if (this.textoMostrado.length < linea.length) {
      clearInterval(this.temporizador);
      this.textoMostrado = linea; // primer Enter: muestra la línea completa
      return;
    }
    if (this.indiceLinea < d.lineas.length - 1) {
      this.indiceLinea++;
      this.escribir();
    } else {
      this.dialogoActivo = null;
    }
  }
}
