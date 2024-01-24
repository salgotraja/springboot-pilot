package io.js.app.bookmarks;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwtBearerAuth")
@Tag(name = "Bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping
    public List<Bookmark> getAllBookmarks() {
        return bookmarkService.getAllBookmarks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bookmark> getBookmarkById(@PathVariable Long id) {
        Bookmark bookmark = bookmarkService.getBookmarkById(id);
        if (bookmark != null) {
            return ResponseEntity.ok(bookmark);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses(
            value = {
                    @ApiResponse(message = "Successfully created new bookmark", code = 201),
                    @ApiResponse(message = "Forbidden", code = 403)
            }
    )
    public Bookmark createBookmark(@RequestBody @Valid Bookmark bookmark) {
        return bookmarkService.createBookmark(bookmark);
    }
}
