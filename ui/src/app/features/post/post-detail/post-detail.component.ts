import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { PostService } from '../../../core/services/post.service';
import { FormsModule } from '@angular/forms';

declare var Quill: any;

@Component({
    selector: 'app-post-detail',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './post-detail.component.html',
    styleUrl: './post-detail.component.css'
})
export class PostDetailComponent implements OnInit, AfterViewInit {
    post: any;
    quill: any;
    replies: { [key: number]: any[] } = {};
    showReplyForm: { [key: number]: boolean } = {};
    replyContent: { [key: number]: string } = {};

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private postService: PostService
    ) { }

    ngOnInit() {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        this.loadPost(id);
    }

    loadPost(id: number) {
        this.postService.getPost(id).subscribe({
            next: (data) => this.post = data,
            error: (err) => console.error('Failed to load post', err)
        });
    }

    toggleFavorite() {
        this.postService.toggleFavorite(this.post.id).subscribe({
            next: () => {
                this.post.isFavorited = !this.post.isFavorited;
                this.post.favoriteCount += this.post.isFavorited ? 1 : -1;
            },
            error: (err) => {
                if (err.status === 401) {
                    this.router.navigate(['/login']);
                }
            }
        });
    }

    ngAfterViewInit() {
        this.initEditor();
    }

    initEditor() {
        setTimeout(() => {
            const editorElement = document.getElementById('comment-editor');
            if (editorElement) {
                this.quill = new Quill('#comment-editor', {
                    theme: 'snow',
                    placeholder: 'Write your answer here...'
                });
            }
        }, 500);
    }

    submitComment() {
        const content = this.quill.root.innerHTML;
        if (content === '<p><br></p>' || content === '') {
            alert('Please enter an answer');
            return;
        }

        this.postService.addComment(this.post.id, content).subscribe({
            next: () => {
                alert('Answer posted!');
                this.quill.setContents([]);
                this.loadPost(this.post.id);
            },
            error: (err) => alert('Failed to post answer: ' + (err.error?.message || 'Unknown error'))
        });
    }

    loadReplies(commentId: number) {
        this.postService.getReplies(commentId).subscribe({
            next: (data) => this.replies[commentId] = data,
            error: (err) => console.error('Failed to load replies', err)
        });
    }

    toggleReplyForm(commentId: number) {
        this.showReplyForm[commentId] = !this.showReplyForm[commentId];
        if (!this.showReplyForm[commentId]) {
            this.replyContent[commentId] = '';
        }
    }

    submitReply(commentId: number) {
        const content = this.replyContent[commentId];
        if (!content || content.trim() === '') {
            alert('Please enter a reply');
            return;
        }

        this.postService.addReply(commentId, content).subscribe({
            next: () => {
                this.showReplyForm[commentId] = false;
                this.replyContent[commentId] = '';
                this.loadReplies(commentId);
                // Also update reply count in UI
                const comment = this.post.comments.find((c: any) => c.id === commentId);
                if (comment) comment.replyCount++;
            },
            error: (err) => alert('Failed to post reply: ' + (err.error?.message || 'Unknown error'))
        });
    }

    replyToUser(commentId: number, userId: number) {
        this.showReplyForm[commentId] = true;
        const mention = `@User #${userId} `;
        if (!this.replyContent[commentId] || !this.replyContent[commentId].startsWith(mention)) {
            this.replyContent[commentId] = mention + (this.replyContent[commentId] || '');
        }
    }
}
