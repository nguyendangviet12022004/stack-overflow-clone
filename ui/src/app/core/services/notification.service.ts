import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { Client, Message } from '@stomp/stompjs';
import { Notification } from '../models/notification.model';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private notificationsSubject = new BehaviorSubject<Notification[]>([]);
    notifications$ = this.notificationsSubject.asObservable();

    private unreadCountSubject = new BehaviorSubject<number>(0);
    unreadCount$ = this.unreadCountSubject.asObservable();

    private stompClient: Client | null = null;
    private readonly baseUrl = 'http://localhost:8080/notification';
    private readonly wsUrl = 'ws://localhost:8080/notification/notifications';

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) {
        this.authService.currentUser$.subscribe((user: any) => {
            if (user) {
                this.loadNotifications();
                this.connectWebSocket();
            } else if (this.authService.isLoggedIn()) {
                // If logged in but no user info, trigger fetch
                this.authService.getUserInfo().subscribe();
            } else {
                this.disconnectWebSocket();
                this.notificationsSubject.next([]);
                this.unreadCountSubject.next(0);
            }
        });
    }

    private loadNotifications() {
        this.http.get<Notification[]>(`${this.baseUrl}/api/notifications`, { withCredentials: true }).subscribe(notifications => {
            this.notificationsSubject.next(notifications);
            this.updateUnreadCount(notifications);
        });
    }

    private connectWebSocket() {
        this.stompClient = new Client({
            brokerURL: this.wsUrl,
            debug: (msg) => console.log('STOMP: ' + msg),
            reconnectDelay: 5000,
        });

        this.stompClient.onConnect = (frame) => {
            console.log('Connected to WebSocket successfully');
            this.stompClient?.subscribe('/user/queue/notifications', (message: Message) => {
                if (message.body) {
                    const notification: Notification = JSON.parse(message.body);
                    this.addNotification(notification);
                }
            });
        };

        this.stompClient.activate();
    }

    private disconnectWebSocket() {
        if (this.stompClient) {
            this.stompClient.deactivate();
            this.stompClient = null;
        }
    }

    private addNotification(notification: Notification) {
        const currentNotifications = this.notificationsSubject.value;
        this.notificationsSubject.next([notification, ...currentNotifications]);
        this.updateUnreadCount([notification, ...currentNotifications]);
    }

    private updateUnreadCount(notifications: Notification[]) {
        const unreadCount = notifications.filter(n => !n.isRead).length;
        this.unreadCountSubject.next(unreadCount);
    }

    markAsRead(notificationId: string) {
        this.http.post(`${this.baseUrl}/api/notifications/${notificationId}/read`, {}, { withCredentials: true }).subscribe(() => {
            const updated = this.notificationsSubject.value.map(n =>
                n.id === notificationId ? { ...n, isRead: true } : n
            );
            this.notificationsSubject.next(updated);
            this.updateUnreadCount(updated);
        });
    }
}
