import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PostService } from '../../core/services/post.service';

@Component({
    selector: 'app-favorites',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './favorites.component.html'
})
export class FavoritesComponent implements OnInit {
    posts: any[] = [];
    loading = true;

    constructor(private postService: PostService) { }

    ngOnInit() {
        this.loadFavorites();
    }

    loadFavorites() {
        this.loading = true;
        this.postService.getFavoritePosts().subscribe({
            next: (data) => {
                this.posts = data;
                this.loading = false;
            },
            error: (err) => {
                console.error('Failed to load favorites', err);
                this.loading = false;
            }
        });
    }

    toggleFavorite(post: any) {
        this.postService.toggleFavorite(post.id).subscribe({
            next: () => {
                // If it was already favorited, remove it from the list since we are on the Favorites page
                this.posts = this.posts.filter(p => p.id !== post.id);
            },
            error: (err) => console.error('Failed to toggle favorite', err)
        });
    }
}
