import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Action } from 'rxjs/internal/scheduler/Action';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { PostService } from '../../core/services/post.service';
import { FormControl, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, RouterModule, ReactiveFormsModule, FormsModule],
    templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
    posts: any[] = [];
    selectedTags: string[] = [];
    searchQuery: string = '';

    // Tag Search properties
    tagInput = new FormControl('');
    tagSuggestions: any[] = [];

    constructor(
        private postService: PostService,
        private route: ActivatedRoute,
        private router: Router
    ) { }

    ngOnInit() {
        this.route.queryParams.subscribe(params => {
            if (params['tag']) {
                this.selectedTags = Array.isArray(params['tag']) ? params['tag'] : [params['tag']];
            } else {
                this.selectedTags = [];
            }
            this.searchQuery = params['query'] || '';
            this.loadPosts(this.selectedTags, this.searchQuery);
        });
    }

    handleTagEnter() {
        const value = this.tagInput.value?.trim();
        if (value) {
            this.toggleTag(value);
            this.tagInput.setValue('');
        }
    }

    loadPosts(tags: string[], query: string) {
        this.postService.searchPosts(tags, query).subscribe({
            next: (data) => this.posts = data,
            error: (err) => console.error('Failed to load posts', err)
        });
    }

    toggleTag(tagName: string) {
        let newTags = [...this.selectedTags];
        const index = newTags.indexOf(tagName);
        if (index > -1) {
            newTags.splice(index, 1);
        } else {
            newTags.push(tagName);
        }
        this.router.navigate(['/'], { queryParams: { tag: newTags, query: this.searchQuery } });
    }

    removeTag(tagName: string) {
        const newTags = this.selectedTags.filter(t => t !== tagName);
        this.router.navigate(['/'], { queryParams: { tag: newTags, query: this.searchQuery } });
    }

    clearFilters() {
        this.router.navigate(['/']);
    }

    toggleFavorite(post: any) {
        this.postService.toggleFavorite(post.id).subscribe({
            next: () => {
                post.isFavorited = !post.isFavorited;
                post.favoriteCount += post.isFavorited ? 1 : -1;
            },
            error: (err) => {
                if (err.status === 401) {
                    this.router.navigate(['/login']);
                }
            }
        });
    }
}
