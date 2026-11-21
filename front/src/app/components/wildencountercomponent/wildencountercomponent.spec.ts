import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Wildencountercomponent } from './wildencountercomponent';

describe('Wildencountercomponent', () => {
  let component: Wildencountercomponent;
  let fixture: ComponentFixture<Wildencountercomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Wildencountercomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Wildencountercomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
