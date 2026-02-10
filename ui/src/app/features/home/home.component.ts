import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PostService } from '../../core/services/post.service';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
    posts: any[] = [];

    constructor(private postService: PostService) { }

    ngOnInit() {
        this.postService.searchPosts().subscribe({
            next: (data) => this.posts = data,
            error: (err) => console.error('Failed to load posts', err)
        });
    }
}
