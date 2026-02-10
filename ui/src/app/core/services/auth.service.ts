import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError, of } from 'rxjs';
import { Router } from '@angular/router';

const API_URL = 'http://localhost:8080/user/auth';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    isLoggedIn = signal<boolean>(this.hasToken());

    constructor(private http: HttpClient, private router: Router) { }

    private hasToken(): boolean {
        return localStorage.getItem('logged_in') === 'true';
    }

    register(data: any): Observable<any> {
        return this.http.post(`${API_URL}/register`, data);
    }

    login(data: any): Observable<any> {
        return this.http.post(`${API_URL}/login`, data).pipe(
            tap(() => {
                this.isLoggedIn.set(true);
                localStorage.setItem('logged_in', 'true');
            })
        );
    }

    logout(): void {
        this.isLoggedIn.set(false);
        localStorage.removeItem('logged_in');
        this.router.navigate(['/login']);
    }

    refreshToken(): Observable<any> {
        return this.http.post(`${API_URL}/refresh`, {}, { withCredentials: true }).pipe(
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
