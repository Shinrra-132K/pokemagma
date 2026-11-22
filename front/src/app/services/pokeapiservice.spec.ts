import { TestBed } from '@angular/core/testing';
import { Pokeapiservice } from './pokeapiservice';

describe('Pokeapiservice', () => {
  let service: Pokeapiservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Pokeapiservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
