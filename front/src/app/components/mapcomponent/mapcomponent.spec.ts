import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Mapcomponent } from './mapcomponent';

describe('Mapcomponent', () => {
  let component: Mapcomponent;
  let fixture: ComponentFixture<Mapcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Mapcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Mapcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
