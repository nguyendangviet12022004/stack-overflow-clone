import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { PostService } from '../../../core/services/post.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';

declare var Quill: any;

@Component({
  selector: 'app-post-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './post-create.component.html',
  styleUrl: './post-create.component.css'
})
export class PostCreateComponent implements AfterViewInit {
  quill: any;
  postForm: FormGroup;
  tagInput = new FormControl('');
  tags: string[] = [];
  suggestions: string[] = [];

  constructor(private fb: FormBuilder, private postService: PostService) {
    this.postForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5)]],
      content: ['', [Validators.required, Validators.minLength(20)]]
    });

    this.tagInput.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        if (query && query.length > 1) {
          return this.postService.searchTags(query);
        } else {
          return of([]);
        }
      })
    ).subscribe((tags: any[]) => {
      this.suggestions = tags.map(t => t.name).filter(name => !this.tags.includes(name));
    });
  }

  ngAfterViewInit() {
    this.quill = new Quill('#editor', {
      theme: 'snow',
      placeholder: 'Compose your question...'
    });

    this.quill.on('text-change', () => {
      const html = this.quill.root.innerHTML;
      this.postForm.get('content')?.setValue(html === '<p><br></p>' ? '' : html);
    });
  }

  addTag(tagName: string) {
    if (tagName && !this.tags.includes(tagName)) {
      this.tags.push(tagName);
    }
    this.tagInput.setValue('');
    this.suggestions = [];
  }

  addTagFromInput() {
    const value = this.tagInput.value?.trim();
    if (value) {
      this.addTag(value);
    }
  }

  removeTag(index: number) {
    this.tags.splice(index, 1);
  }

  onSubmit() {
    if (this.postForm.valid) {
      const postData = {
        title: this.postForm.value.title,
        content: this.postForm.value.content,
        tags: this.tags
      };

      this.postService.createPost(postData).subscribe({
        next: () => {
          alert('Post created successfully!');
          this.postForm.reset();
          this.quill.setContents([]);
          this.tags = [];
        },
        error: (err) => alert('Failed to create post: ' + (err.error?.message || 'Unknown error'))
      });
    }
  }
}
