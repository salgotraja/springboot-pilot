package io.js.jdbcclient;

import io.js.ContainersConfig;
import io.js.models.Bookmark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@JdbcTest
@AutoConfigureTestDatabase(replace = NONE)
@Import(ContainersConfig.class)
@Sql("classpath:/test_data.sql")
class BookmarkRepositoryTest {
    @Autowired
    JdbcClient jdbcClient;

    BookmarkRepository bookmarkRepository;

    @BeforeEach
    void setUp() {
        bookmarkRepository = new BookmarkRepository(jdbcClient);
    }

    @Test
    void shouldFindAllBookmarks(){
        List<Bookmark> bookmarks = bookmarkRepository.findAll();
        assertThat(bookmarks).isNotEmpty();
        assertThat(bookmarks).hasSize(6);
    }

    @Test
    void shouldCreateBookmark() {
        Bookmark bookmark = new Bookmark(null, "Test Bookmark", "https://www.test.com", Instant.now());
        Long id = bookmarkRepository.save(bookmark);
        assertThat(id).isNotNull();
    }

    @Test
    void shouldGetBookmarkById() {
        Bookmark bookmark = new Bookmark(null, "Test Bookmark", "https://www.test.com", Instant.now());
        Long id = bookmarkRepository.save(bookmark);

        Optional<Bookmark> bookmarkOptional = bookmarkRepository.findById(id);
        assertThat(bookmarkOptional).isPresent();
        assertThat(bookmarkOptional.get().id()).isEqualTo(id);
        assertThat(bookmarkOptional.get().title()).isEqualTo("Test Bookmark");
        assertThat(bookmarkOptional.get().url()).isEqualTo("https://www.test.com");
        assertThat(bookmarkOptional.get().createdAt()).isNotNull();
    }

    @Test
    void shouldEmptyWhenBookmarkNotFound() {
        Optional<Bookmark> bookmarkOptional = bookmarkRepository.findById(100L);
        assertThat(bookmarkOptional).isEmpty();
    }

    @Test
    void shouldUpdateBookmark() {
        Bookmark bookmark = new Bookmark(null, "Test Bookmark", "https://www.test.com", Instant.now());
        Long id = bookmarkRepository.save(bookmark);

        Bookmark changedBookmark = new Bookmark(id, "My Updated Title", "https://www.test.com", bookmark.createdAt());
        bookmarkRepository.update(changedBookmark);

        Bookmark updatedBookmark = bookmarkRepository.findById(id).orElseThrow();
        assertThat(updatedBookmark.id()).isEqualTo(changedBookmark.id());
        assertThat(updatedBookmark.title()).isEqualTo(changedBookmark.title());
        assertThat(updatedBookmark.url()).isEqualTo(changedBookmark.url());
    }

    @Test
    void shouldDeleteBookmark() {
        Bookmark bookmark = new Bookmark(null, "Test Bookmark", "https://www.test.com", Instant.now());
        Long id = bookmarkRepository.save(bookmark);

        bookmarkRepository.delete(id);

        Optional<Bookmark> optionalBookmark = bookmarkRepository.findById(id);
        assertThat(optionalBookmark).isEmpty();
    }
}