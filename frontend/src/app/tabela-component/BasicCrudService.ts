import { Observable } from 'rxjs';

export interface Entity {
  id: number | null;
}

export interface CrudService<T extends Entity> {
  list(): Observable<T[]>;
  find(query: string): Observable<T[]>;
  save(valor: T): Observable<T>;
  update(valor: T): Observable<T>;
  delete(id: number): Observable<void>;
}
