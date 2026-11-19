import { Component, ElementRef, HostListener, NgZone, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { Mapaservice } from '../../services/mapaservice';
import { Mapadatosmodel } from '../../models/mapadatosmodel';
import { Direccionmodel, Dialogomodel, Jugadormodel, Npcmodel } from '../../models/npcmodel';



@Component({
  selector: 'app-mapcomponent',
  standalone: false,
  styleUrl: './mapcomponent.css',
  templateUrl: './mapcomponent.html',
})
export class Mapcomponent implements OnInit, OnDestroy {
  @ViewChild('lienzo', { static: true }) lienzo!: ElementRef<HTMLCanvasElement>;

  private mapaservice = inject(Mapaservice);
  private zone = inject(NgZone);

  // Pantalla estilo GBA: 240x160 px, el CSS la escala
  private readonly ANCHO = 240;
  private readonly ALTO = 160;
  private readonly DURACION_PASO = 250; // ms por tile

  private ctx!: CanvasRenderingContext2D;
  private datos!: Mapadatosmodel;
  private fondo = new Image();
  private spriteJugador = new Image();
  private spritesNpc = new Map<string, HTMLImageElement>();
  private idAnimacion = 0;
  private ultimoTiempo = 0;
  private teclas: Direccionmodel[] = [];
  private temporizador: ReturnType<typeof setInterval> | undefined;

  // Puntos de salida en Pueblo Raíz (col 128, fila 167 es una celda libre)
  jugador: Jugadormodel = { col: 128, fila: 167, direccion: 'abajo', moviendo: false, progreso: 0, paso: 0 };

  npcs: Npcmodel[] = [
    {
      id: 'abedul', nombre: 'Prof. Abedul', col: 124, fila: 167, direccion: 'abajo',
      sprite: '/assets/sprites/normal/002.png',
      dialogo: ['¡Hola! Bienvenido al mundo Pokémon.', 'Pasa por mi laboratorio cuando estés listo.']
    }
  ];

  dialogoActivo: Dialogomodel | null = null;
  textoMostrado = '';
  private indiceLinea = 0;

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

  ngOnDestroy(): void {
    cancelAnimationFrame(this.idAnimacion);
    clearInterval(this.temporizador);
  }

  // ---------- entrada ----------
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

  @HostListener('window:keyup', ['$event'])
  alSoltar(e: KeyboardEvent): void {
    const dir = this.teclaADireccion(e.key);
    if (dir) this.teclas = this.teclas.filter(t => t !== dir);
  }

  private teclaADireccion(tecla: string): Direccionmodel | null {
    switch (tecla.toLowerCase()) {
      case 'arrowup': case 'w': return 'arriba';
      case 'arrowdown': case 's': return 'abajo';
      case 'arrowleft': case 'a': return 'izquierda';
      case 'arrowright': case 'd': return 'derecha';
      default: return null;
    }
  }

  // ---------- bucle del juego ----------
  private bucle = (t: number): void => {
    const dt = Math.min(t - this.ultimoTiempo, 50);
    this.ultimoTiempo = t;
    this.actualizar(dt);
    this.dibujar();
    this.idAnimacion = requestAnimationFrame(this.bucle);
  };

  private vector(dir: Direccionmodel): { dc: number; df: number } {
    switch (dir) {
      case 'arriba': return { dc: 0, df: -1 };
      case 'abajo': return { dc: 0, df: 1 };
      case 'izquierda': return { dc: -1, df: 0 };
      default: return { dc: 1, df: 0 };
    }
  }

  private npcEn(col: number, fila: number): Npcmodel | undefined {
    return this.npcs.find(n => n.col === col && n.fila === fila);
  }

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

  // ---------- dibujo ----------
  private dibujar(): void {
    const ctx = this.ctx;
    const T = this.datos.tamanoTile;
    const j = this.jugador;
    const v = this.vector(j.direccion);

    const px = (j.col + (j.moviendo ? v.dc * j.progreso : 0)) * T;
    const py = (j.fila + (j.moviendo ? v.df * j.progreso : 0)) * T;

    // La cámara sigue al jugador sin salirse del mapa
    const camX = Math.round(Math.max(0, Math.min(px + T / 2 - this.ANCHO / 2, this.fondo.width - this.ANCHO)));
    const camY = Math.round(Math.max(0, Math.min(py + T / 2 - this.ALTO / 2, this.fondo.height - this.ALTO)));

    ctx.imageSmoothingEnabled = false;
    ctx.clearRect(0, 0, this.ANCHO, this.ALTO);
    ctx.drawImage(this.fondo, camX, camY, this.ANCHO, this.ALTO, 0, 0, this.ANCHO, this.ALTO);

    // Se dibujan de arriba hacia abajo para que quien está más abajo quede delante
    const entidades = [
      ...this.npcs.map(n => ({ y: n.fila * T, dibujar: () => this.dibujarNpc(n, camX, camY) })),
      { y: py, dibujar: () => this.dibujarJugador(px, py, camX, camY) }
    ].sort((a, b) => a.y - b.y);
    entidades.forEach(e => e.dibujar());
  }

  private dibujarJugador(px: number, py: number, camX: number, camY: number): void {
    const j = this.jugador;
    this.dibujarSprite(this.spriteJugador, j.direccion, j.moviendo, j.paso, px - camX, py - camY);
  }

  private dibujarNpc(n: Npcmodel, camX: number, camY: number): void {
    const img = this.spritesNpc.get(n.id);
    if (img) this.dibujarSprite(img, n.direccion, false, 0, n.col * 16 - camX, n.fila * 16 - camY);
  }

  /**
   * Hoja de 9 cuadros de 16x32 (144x32 px), orden estándar de Esmeralda/Rubí:
   * 0 abajo, 1 arriba, 2 izquierda, 3-4 caminar abajo, 5-6 caminar arriba, 7-8 caminar izquierda.
   * La derecha es la izquierda en espejo.
   */
  private dibujarSprite(img: HTMLImageElement, dir: Direccionmodel, moviendo: boolean, paso: number, x: number, y: number): void {
    if (!img.complete || img.naturalWidth === 0) return;
    let cuadro = 0;
    if (dir === 'abajo') cuadro = moviendo ? 3 + paso : 0;
    if (dir === 'arriba') cuadro = moviendo ? 5 + paso : 1;
    if (dir === 'izquierda' || dir === 'derecha') cuadro = moviendo ? 7 + paso : 2;

    const ctx = this.ctx;
    const destY = y - 16; // el sprite mide 32 de alto y los pies quedan en el tile
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

  // ---------- diálogos ----------
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

  private opuesta(d: Direccionmodel): Direccionmodel {
    return ({ arriba: 'abajo', abajo: 'arriba', izquierda: 'derecha', derecha: 'izquierda' } as const)[d];
  }

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
  }}
