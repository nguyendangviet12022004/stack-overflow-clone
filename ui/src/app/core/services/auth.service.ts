import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError, of, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

const API_URL = 'http://localhost:8080/user/auth';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    isLoggedIn = signal<boolean>(this.hasToken());
    private currentUserSubject = new BehaviorSubject<any>(null);
    currentUser$ = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient, private router: Router) { }

    private hasToken(): boolean {
        return localStorage.getItem('logged_in') === 'true';
    }

    register(data: any): Observable<string> {
        return this.http.post(`${API_URL}/register`, data, { withCredentials: true, responseType: 'text' });
    }

    login(data: any): Observable<string> {
        return this.http.post(`${API_URL}/login`, data, { withCredentials: true, responseType: 'text' }).pipe(
            tap(() => {
                this.isLoggedIn.set(true);
                localStorage.setItem('logged_in', 'true');
                this.getUserInfo().subscribe();
            })
        );
    }

    getUserInfo(): Observable<any> {
        return this.http.get<any>(`http://localhost:8080/user/users/me`, { withCredentials: true }).pipe(
            tap(user => this.currentUserSubject.next(user)),
            catchError(err => {
                this.logout();
                return throwError(() => err);
            })
        );
    }

    logout(): void {
        this.isLoggedIn.set(false);
        this.currentUserSubject.next(null);
        localStorage.removeItem('logged_in');
        this.router.navigate(['/login']);
    }

    refreshToken(): Observable<string> {
        return this.http.post(`${API_URL}/refresh`, {}, { withCredentials: true, responseType: 'text' }).pipe(
            tap(() => {
                this.isLoggedIn.set(true);
            }),
            catchError(err => {
                this.logout();
                return throwError(() => err);
            })
        );
    }
}
