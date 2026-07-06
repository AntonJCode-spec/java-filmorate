package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FilmorateApplicationTests {

    private static final String BASE = "http://localhost:8080";
    private static HttpClient client;

    @Autowired
    private ObjectMapper objectMapper;


	@Test
	void contextLoads() {
	}

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newHttpClient();
    }

    @Test
    void postFilmsShouldReturnCode400IfRequestEmpty() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(400, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode200IfRequestCorrect() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(2005, 7, 1));
        film.setDuration(200);

        String json = objectMapper.writeValueAsString(film);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode500IfNameNull() throws Exception {
        Film film = new Film();
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(2005, 7, 1));
        film.setDuration(200);

        String json = objectMapper.writeValueAsString(film);
        System.out.println(json);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode500IfDescriptionSize201() throws Exception {
        String description = "a".repeat(201);
        Film film = new Film();
        film.setName("qwe");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(2005, 7, 1));
        film.setDuration(200);

        String json = objectMapper.writeValueAsString(film);
        System.out.println(json);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode200IfDescriptionSize200() throws Exception {
        String description = "a".repeat(200);
        Film film = new Film();
        film.setName("qwe");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(2005, 7, 1));
        film.setDuration(200);

        String json = objectMapper.writeValueAsString(film);
        System.out.println(json);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode500IfBirthdayIncorrect() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(200);

        String json = objectMapper.writeValueAsString(film);
        System.out.println(json);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode200IfBirthdayCorrect() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(200);

        String json = objectMapper.writeValueAsString(film);
        System.out.println(json);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode500IfDurationNegative() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(-200);

        String json = objectMapper.writeValueAsString(film);
        System.out.println(json);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode500IfDurationZero() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(0);

        String json = objectMapper.writeValueAsString(film);
        System.out.println(json);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void putFilmsShouldReturn500IfIdNull() throws Exception {
        Film film = new Film();
        film.setName("Zyza");
        film.setDescription("PyPyPy");

        String json = objectMapper.writeValueAsString(film);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void putFilmsShouldReturn200IfIdCorrect() throws Exception {
        String jsonFilm = "{\"name\": \"PyPyPy\", \"duration\":100}";
        HttpRequest reqFilms = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm))
                .build();
        client.send(reqFilms, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        Film film = new Film();
        film.setId(1L);
        film.setName("Zyza");
        film.setDescription("PyPyPy");

        String json = objectMapper.writeValueAsString(film);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void putFilmsShouldReturn500IfIdNotExist() throws Exception {
        String jsonFilm = "{\"name\": \"PyPyPy\", \"duration\":100}";
        HttpRequest reqFilms = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm))
                .build();
        client.send(reqFilms, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        Film film = new Film();
        film.setId(-1L);
        film.setName("Zyza");
        film.setDescription("PyPyPy");

        String json = objectMapper.writeValueAsString(film);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void postUsersShouldReturn200IfRequestCorrect() throws Exception {
        User user = new User();
        user.setEmail("yandex@mail");
        user.setLogin("login");
        user.setBirthday(LocalDate.now());

        String json = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void postUsersShouldReturn500IfEmailNull() throws Exception {
        User user = new User();
        user.setLogin("login");

        String json = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void postUsersShouldReturn500IfEmailWithoutAt() throws Exception {
        User user = new User();
        user.setEmail("yandex.mail");
        user.setLogin("login");

        String json = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void postUsersShouldReturn500IfLoginNull() throws Exception {
        User user = new User();
        user.setEmail("yandex@mail");

        String json = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void postUsersShouldReturn500IfBirthdayInFeature() throws Exception {
        User user = new User();
        user.setEmail("yandex@mail");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(3000, 10, 12));

        String json = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void putUsersShouldReturn200IfIdExist() throws Exception {
        String jsonFilm = "{\"email\": \"PyPyPy@mail\", \"login\": \"login\"}";
        HttpRequest reqFilms = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm))
                .build();
        client.send(reqFilms, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        User user = new User();
        user.setId(1L);
        user.setEmail("yandex@mail");
        user.setLogin("syrikat");
        user.setName("py py py");
        user.setBirthday(LocalDate.of(2005, 10, 12));

        String json = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void putUsersShouldReturn500IfIdNotExist() throws Exception {
        String jsonFilm = "{\"email\": \"PyPyPy@mail\", \"login\": \"login\"}";
        HttpRequest reqFilms = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm))
                .build();
        client.send(reqFilms, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        User user = new User();
        user.setId(-1L);
        user.setEmail("yandex@mail");
        user.setLogin("syrikat");
        user.setName("py py py");
        user.setBirthday(LocalDate.of(2005, 10, 12));

        String json = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }

    @Test
    void putUsersShouldReturn500IfIdNull() throws Exception {
        String jsonFilm = "{\"email\": \"PyPyPy@mail\", \"login\": \"login\"}";
        HttpRequest reqFilms = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonFilm))
                .build();
        client.send(reqFilms, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        User user = new User();
        user.setEmail("yandex@mail");
        user.setLogin("syrikat");
        user.setName("py py py");
        user.setBirthday(LocalDate.of(2005, 10, 12));

        String json = objectMapper.writeValueAsString(user);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/users"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(500, response.statusCode());
    }








}
