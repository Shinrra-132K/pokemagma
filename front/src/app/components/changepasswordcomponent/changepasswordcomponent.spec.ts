import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Changepasswordcomponent } from './changepasswordcomponent';

describe('Changepasswordcomponent', () => {
  let component: Changepasswordcomponent;
  let fixture: ComponentFixture<Changepasswordcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Changepasswordcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Changepasswordcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
