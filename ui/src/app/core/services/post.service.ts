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

    getPost(id: number): Observable<any> {
        return this.http.get<any>(`${API_URL}/${id}`, { withCredentials: true });
    }

    searchPosts(tag: string = ''): Observable<any[]> {
        return this.http.get<any[]>(`${API_URL}?tag=${tag}`, { withCredentials: true });
    }

    searchTags(query: string): Observable<any[]> {
        return this.http.get<any[]>(`${API_URL}/tags?query=${query}`, { withCredentials: true });
    }

    addComment(postId: number, content: string): Observable<any> {
        return this.http.post(`${API_URL}/${postId}/comments`, { content }, { withCredentials: true });
    }

    getReplies(commentId: number): Observable<any[]> {
        return this.http.get<any[]>(`${API_URL}/comments/${commentId}/replies`, { withCredentials: true });
    }

    addReply(commentId: number, content: string): Observable<any> {
        return this.http.post(`${API_URL}/comments/${commentId}/replies`, { content }, { withCredentials: true });
    }
}
