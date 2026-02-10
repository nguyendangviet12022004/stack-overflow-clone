import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/post/posts';

@Injectable({
    providedIn: 'root'
})
export class PostService {

    constructor(private http: HttpClient) { }

    createPost(post: any): Observable<any> {
        return this.http.post(API_URL, post, { withCredentials: true });
    }

    searchPosts(tag: string = ''): Observable<any[]> {
        return this.http.get<any[]>(`${API_URL}?tag=${tag}`, { withCredentials: true });
    }

    searchTags(query: string): Observable<any[]> {
        return this.http.get<any[]>(`${API_URL}/tags?query=${query}`, { withCredentials: true });
    }
}
