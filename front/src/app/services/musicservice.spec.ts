import { TestBed } from '@angular/core/testing';
import { Musicservice } from './musicservice';

describe('Musicservice', () => {
  let service: Musicservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Musicservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
