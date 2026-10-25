import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Startcomponent } from './startcomponent';

describe('Startcomponent', () => {
  let component: Startcomponent;
  let fixture: ComponentFixture<Startcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Startcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Startcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
