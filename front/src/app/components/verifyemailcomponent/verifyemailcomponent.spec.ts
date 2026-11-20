import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Verifyemailcomponent } from './verifyemailcomponent';

describe('Verifyemailcomponent', () => {
  let component: Verifyemailcomponent;
  let fixture: ComponentFixture<Verifyemailcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Verifyemailcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Verifyemailcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
