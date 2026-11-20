import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Introcomponent } from './introcomponent';

describe('Introcomponent', () => {
  let component: Introcomponent;
  let fixture: ComponentFixture<Introcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Introcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Introcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
