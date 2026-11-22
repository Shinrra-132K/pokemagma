import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Pokedexcomponent } from './pokedexcomponent';

describe('Pokedexcomponent', () => {
  let component: Pokedexcomponent;
  let fixture: ComponentFixture<Pokedexcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Pokedexcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Pokedexcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
