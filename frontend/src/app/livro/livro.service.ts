import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CrudService } from '../tabela-component/BasicCrudService';

export interface Livro {
  id: number | null;
  titulo: string;
  autor: string;
  isbn: string;
  dataPublicacao: Date;
  categoria: string[];
}

@Injectable({
  providedIn: 'root',
})
export class LivroService implements CrudService<Livro> {
  private readonly API = 'http://localhost:8080/livros';

  constructor(private http: HttpClient) {}

  list(): Observable<Livro[]> {
    return this.http.get<Livro[]>(this.API);
  }

  find(query: string): Observable<Livro[]> {
    return this.http.get<Livro[]>(`${this.API}/${query}`);
  }

  save(livro: Livro): Observable<Livro> {
    return this.http.post<Livro>(this.API, livro);
  }

  update(livro: Livro): Observable<Livro> {
    return this.http.put<Livro>(this.API, livro);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  listFromGoogleBooks(query: string): Observable<Livro[]> {
    return this.http.get<Livro[]>(`${this.API}/google/${query}/0`);
  }

  listFromRecomendacao(usuarioId: number): Observable<Livro[]> {
    return this.http.get<Livro[]>(`${this.API}/recomendar/${usuarioId}`);
  }
}
