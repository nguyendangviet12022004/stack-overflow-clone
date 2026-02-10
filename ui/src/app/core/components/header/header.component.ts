import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './header.component.html'
})
export class HeaderComponent {
    searchQuery: string = '';

    constructor(
        public authService: AuthService,
        private router: Router
    ) { }

    onSearch() {
        if (this.searchQuery.trim()) {
            this.router.navigate(['/'], { queryParams: { query: this.searchQuery } });
            this.searchQuery = '';
        }
    }
}
