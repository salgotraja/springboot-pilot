package io.js.api;

import io.js.AbstractIntegrationTest;
import io.js.models.Bookmark;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;

import static io.restassured.RestAssured.given;

@Sql("classpath:/test_data.sql")
class BookmarkControllerTest extends AbstractIntegrationTest {

    @Test
    void shouldGetAllBookmarks(){
        given()
                .when()
                .get("/api/bookmarks")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldGetBookmarkById() {
        given()
                .when()
                .get("/api/bookmarks/{id}", 1)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldCreateBookmark() {
        Bookmark bookmark = new Bookmark(null,"Title", "URL", Instant.now());
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(bookmark)
                .when()
                .post("/api/bookmarks")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void shouldUpdateBookmark() {
        Bookmark bookmark = new Bookmark(null, "Updated Title", "Updated URL", Instant.now());
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(bookmark)
                .when()
                .put("/api/bookmarks/{id}", 1)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void shouldDeleteBookmark() {
        given()
                .when()
                .delete("/api/bookmarks/{id}", 1)
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}