import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

interface PokemonApiResponse {
  name: string;
  sprites: {
    front_default: string | null;
  };
}

export interface PokemonInicial {
  id: number;
  nombre: string;
  sprite: string;
  pokeball: string;
}

@Injectable({ providedIn: 'root' })
export class Pokeapiservice {
  private http = inject(HttpClient);
  private readonly url = 'https://pokeapi.co/api/v2/pokemon';

  obtenerPokemon(id: number): Observable<PokemonInicial> {
    return this.http
      .get<PokemonApiResponse>(`${this.url}/${id}`)
      .pipe(
        map(pokemon => ({
          id,
          nombre: pokemon.name,
          sprite:
            pokemon.sprites.front_default ??
            `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${id}.png`,
          pokeball: '/assets/images/pokeicon.png',
        }))
      );
  }
}
