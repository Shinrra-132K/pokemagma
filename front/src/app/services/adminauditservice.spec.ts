import { TestBed } from '@angular/core/testing';
import { Adminauditservice } from './adminauditservice';

describe('Adminauditservice', () => {
  let service: Adminauditservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Adminauditservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
