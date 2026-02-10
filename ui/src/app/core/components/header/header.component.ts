import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { NotificationService } from '../../services/notification.service';

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './header.component.html'
})
export class HeaderComponent {
    searchQuery: string = '';
    showNotifications: boolean = false;

    constructor(
        public authService: AuthService,
        public notificationService: NotificationService,
        private router: Router
    ) { }

    onSearch() {
        if (this.searchQuery.trim()) {
            this.router.navigate(['/'], { queryParams: { query: this.searchQuery } });
            this.searchQuery = '';
        }
    }

    toggleNotifications() {
        this.showNotifications = !this.showNotifications;
        if (this.showNotifications) {
            this.notificationService.markAllAsRead();
        }
    }

    markAsRead(id: string, event: Event) {
        event.stopPropagation();
        this.notificationService.markAsRead(id);
    }
}
