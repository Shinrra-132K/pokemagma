import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Profilecomponent } from './profilecomponent';

describe('Profilecomponent', () => {
  let component: Profilecomponent;
  let fixture: ComponentFixture<Profilecomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Profilecomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Profilecomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
