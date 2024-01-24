package io.js.app.bookmarks;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookmarkNotFoundException  extends RuntimeException{
    public BookmarkNotFoundException(Long id) {
        super("Bookmark with id="+id+" not found");
    }
}
