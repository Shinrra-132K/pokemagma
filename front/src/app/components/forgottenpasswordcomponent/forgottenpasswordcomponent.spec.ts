import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Forgottenpasswordcomponent } from './forgottenpasswordcomponent';

describe('Forgottenpasswordcomponent', () => {
  let component: Forgottenpasswordcomponent;
  let fixture: ComponentFixture<Forgottenpasswordcomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Forgottenpasswordcomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Forgottenpasswordcomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
