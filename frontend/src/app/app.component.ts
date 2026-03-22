import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router, RouterOutlet } from '@angular/router';

interface Menu {
  name: string;
  icon: string;
  route: string;
}
@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    MatButtonModule,
    MatSidenavModule,
    MatTabsModule,
    MatToolbarModule,
    MatIconModule,
    MatListModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'frontend';

  private _router = inject(Router);

  protected menus: Menu[] = [
    { name: 'Usuário', icon: 'person', route: '/usuario' },
    { name: 'Livro', icon: 'menu_book', route: '/livro' },
    { name: 'Empréstimo', icon: 'assignment_return', route: '/emprestimo' },
  ];

  protected navigateTo(route: string) {
    this._router.navigate([route]);
  }
}
