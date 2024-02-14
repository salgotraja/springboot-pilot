package io.js.api;

import io.js.jdbcclient.BookmarkRepository;
import io.js.models.Bookmark;
import io.js.models.CreateBookmarkRequest;
import io.js.models.UpdateBookmarkRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final BookmarkRepository repository;

    public BookmarkController(BookmarkRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Bookmark> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bookmark> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody CreateBookmarkRequest bookmark) {
        var b = new Bookmark(null, bookmark.title(), bookmark.url(), Instant.now());
        repository.save(b);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UpdateBookmarkRequest request) {
        var bookmark = repository.findById(id);
        if (bookmark.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        repository.update(new Bookmark(id, request.title(), request.url(), bookmark.get().createdAt()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.delete(id);
    }
}
