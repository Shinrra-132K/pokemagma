import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Bagcomponent } from './bagcomponent';

describe('Bagcomponent', () => {
  let component: Bagcomponent;
  let fixture: ComponentFixture<Bagcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Bagcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Bagcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
