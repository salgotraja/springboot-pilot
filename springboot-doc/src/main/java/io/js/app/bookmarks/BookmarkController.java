package io.js.app.bookmarks;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping
    public List<Bookmark> getAllBookmarks() {
        return bookmarkService.getAllBookmarks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bookmark> getBookmarkById(@PathVariable Long id) {
        log.info("id: {}", id);
        Bookmark bookmark = bookmarkService.getBookmarkById(id);
        log.info("bookmark: {}", bookmark);
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
                    @ApiResponse(description = "Successfully created new bookmark", responseCode = "201"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
            }
    )
    public Bookmark createBookmark(@RequestBody @Valid Bookmark bookmark) {
        return bookmarkService.createBookmark(bookmark);
    }
}