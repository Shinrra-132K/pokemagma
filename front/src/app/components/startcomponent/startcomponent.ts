import { Component, OnInit, inject } from '@angular/core';
import { Musicservice } from '../../services/musicservice';

@Component({
  selector: 'app-startcomponent',
  standalone: false,
  styleUrl: './startcomponent.css',
  templateUrl: './startcomponent.html',
})
export class Startcomponent implements OnInit {
  private musica = inject(Musicservice);

  ngOnInit(): void {
    this.musica.reproducirCuandoSePueda('/assets/music/introductions.mp3');
  }
  mostrarOpciones = false;

  cambiarOpciones(){
    this.mostrarOpciones = !this.mostrarOpciones;
  }

}
