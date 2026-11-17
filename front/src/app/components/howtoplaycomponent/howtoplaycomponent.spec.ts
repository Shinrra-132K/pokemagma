import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Howtoplaycomponent } from './howtoplaycomponent';

describe('Howtoplaycomponent', () => {
  let component: Howtoplaycomponent;
  let fixture: ComponentFixture<Howtoplaycomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Howtoplaycomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Howtoplaycomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
