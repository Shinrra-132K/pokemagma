import { TestBed } from '@angular/core/testing';
import { Playergameservice } from './playergameservice';

describe('Playergameservice', () => {
  let service: Playergameservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Playergameservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
