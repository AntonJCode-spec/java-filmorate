package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FilmorateApplicationTests {

    private static final String BASE = "http://localhost:8080";
    private static HttpClient client;
    private static final int OK = 200;
    private static final int NO_CONTENT = 204;
    private static final int BAD_REQUEST = 400;
    private static final int NOT_FOUND = 404;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final List<User> userList = new ArrayList<>();
    private static final List<Film> filmList = new ArrayList<>();


    @Autowired
    private TestClient testClient;

    @Autowired
    private ObjectMapper objectMapper;


	@Test
	void contextLoads() {
	}

    @BeforeAll
    static void beforeAll() {
        client = HttpClient.newHttpClient();
        for (int i = 1; i < 11; i++) {
            User user = new User();
            user.setId((long)i);
            user.setEmail("email" + i + "@mail");
            user.setLogin("login" + i);
            user.setName("name" + i);
            userList.add(user);
            Film film = new Film();
            film.setId((long)i);
            film.setName("film" + i);
            film.setDuration(i);
            filmList.add(film);
        }

    }

    @BeforeEach
    void beforeEach() throws Exception {
        testClient.deleteUser("/clear");
        testClient.deleteFilms("/clear");
        for (int i = 0; i < 10; i++) {
            testClient.postUsers(userList.get(i));
            testClient.postFilms(filmList.get(i));
        }


    }

    @Test
    void postFilmsShouldReturnCode500IfRequestEmpty() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        Assertions.assertEquals(INTERNAL_SERVER_ERROR, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode200IfRequestCorrect() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(2005, 7, 1));
        film.setDuration(200);

        HttpResponse<String> response = testClient.postFilms(film);
        Assertions.assertEquals(OK, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode400IfNameNull() throws Exception {
        Film film = new Film();
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(2005, 7, 1));
        film.setDuration(200);

        HttpResponse<String> response = testClient.postFilms(film);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode400IfDescriptionSize201() throws Exception {
        String description = "a".repeat(201);
        Film film = new Film();
        film.setName("qwe");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(2005, 7, 1));
        film.setDuration(200);

        HttpResponse<String> response = testClient.postFilms(film);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode200IfDescriptionSize200() throws Exception {
        String description = "a".repeat(200);
        Film film = new Film();
        film.setName("qwe");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(2005, 7, 1));
        film.setDuration(200);

        HttpResponse<String> response = testClient.postFilms(film);
        Assertions.assertEquals(OK, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode400IfBirthdayIncorrect() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(200);

        HttpResponse<String> response = testClient.postFilms(film);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode200IfBirthdayCorrect() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(200);

        HttpResponse<String> response = testClient.postFilms(film);
        Assertions.assertEquals(OK, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode400IfDurationNegative() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(-200);

        HttpResponse<String> response = testClient.postFilms(film);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void postFilmsShouldReturnCode400IfDurationZero() throws Exception {
        Film film = new Film();
        film.setName("qwe");
        film.setDescription("asd");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(0);

        HttpResponse<String> response = testClient.postFilms(film);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void putFilmsShouldReturn400IfIdNull() throws Exception {
        Film film = new Film();
        film.setName("Zyza");
        film.setDescription("PyPyPy");

        HttpResponse<String> response = testClient.putFilms(film);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void putFilmsShouldReturn200IfIdCorrect() throws Exception {
        Film firstFilm = new Film();
        firstFilm.setName("PyPyPy");
        firstFilm.setDuration(100);

        HttpResponse<String> resp1 = testClient.postFilms(firstFilm);

        Film filmToUpdate = new Film();
        filmToUpdate.setId(1L);
        filmToUpdate.setName("Zyza");
        filmToUpdate.setDescription("PyPyPy");

        HttpResponse<String> response = testClient.putFilms(filmToUpdate);
        Assertions.assertEquals(OK, response.statusCode());
    }

    @Test
    void putFilmsShouldReturn404IfIdNotExist() throws Exception {
        Film firstFilm = new Film();
        firstFilm.setName("taraadiofg");
        firstFilm.setDuration(100);

        HttpResponse<String> resp1 = testClient.postFilms(firstFilm);

        Film film = new Film();
        film.setId(-1L);
        film.setName("Zyza");
        film.setDescription("PyPyPy");

        HttpResponse<String> response = testClient.putFilms(film);
        Assertions.assertEquals(NOT_FOUND, response.statusCode());
    }

    @Test
    void postUsersShouldReturn200IfRequestCorrect() throws Exception {
        User user = new User();
        user.setEmail("yandex@mail");
        user.setLogin("login");
        user.setBirthday(LocalDate.now());

        HttpResponse<String> response = testClient.postUsers(user);
        Assertions.assertEquals(OK, response.statusCode());
    }

    @Test
    void postUsersShouldReturn400IfEmailNull() throws Exception {
        User user = new User();
        user.setLogin("login");

        HttpResponse<String> response = testClient.postUsers(user);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void postUsersShouldReturn400IfEmailWithoutAt() throws Exception {
        User user = new User();
        user.setEmail("yandex.mail");
        user.setLogin("login");

        HttpResponse<String> response = testClient.postUsers(user);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void postUsersShouldReturn400IfLoginNull() throws Exception {
        User user = new User();
        user.setEmail("yandex@mail");

        HttpResponse<String> response = testClient.postUsers(user);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void postUsersShouldReturn400IfBirthdayInFeature() throws Exception {
        User user = new User();
        user.setEmail("yandex@mail");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(3000, 10, 12));

        HttpResponse<String> response = testClient.postUsers(user);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void putUsersShouldReturn200IfIdExist() throws Exception {
        User firstUser = new User();
        firstUser.setEmail("SRFGdsfgPyPyPy@mail");
        firstUser.setLogin("login");

        HttpResponse<String> resp1 = testClient.postUsers(firstUser);

        User user = new User();
        user.setId(objectMapper.readValue(resp1.body(), User.class).getId());
        user.setEmail("yandex@mail");
        user.setLogin("syrikat");
        user.setName("py py py");
        user.setBirthday(LocalDate.of(2005, 10, 12));

        HttpResponse<String> response = testClient.putUsers(user);
        Assertions.assertEquals(OK, response.statusCode());
        Assertions.assertEquals(user, objectMapper.readValue(response.body(), User.class));
    }

    @Test
    void putUsersShouldReturn404IfIdNotExist() throws Exception {
        User firstUser = new User();
        firstUser.setEmail("srhggdivinvoidfbod@mail");
        firstUser.setLogin("login");

        testClient.postUsers(firstUser);

        User user = new User();
        user.setId(-1L);
        user.setEmail("yandex@mail");
        user.setLogin("syrikat");
        user.setName("py py py");
        user.setBirthday(LocalDate.of(2005, 10, 12));

        HttpResponse<String> response = testClient.putUsers(user);
        Assertions.assertEquals(NOT_FOUND, response.statusCode());
    }

    @Test
    void putUsersShouldReturn400IfIdNull() throws Exception {
        User firstUser = new User();
        firstUser.setEmail("sgvipbopibdfomrOPgo@mail");
        firstUser.setLogin("login");

        testClient.postUsers(firstUser);

        User user = new User();
        user.setEmail("yandex@mail");
        user.setLogin("syrikat");
        user.setName("py py py");
        user.setBirthday(LocalDate.of(2005, 10, 12));

        HttpResponse<String> response = testClient.putUsers(user);
        Assertions.assertEquals(BAD_REQUEST, response.statusCode());
    }

    @Test
    void getFilmByIdShouldReturnCorrectFilm() throws Exception {
        String filmName = "srgswgergeghdhsdth";
        Film film = new Film();
        film.setName(filmName);

        HttpResponse<String> resp1 = testClient.postFilms(film);
        Long filmId = objectMapper.readValue(resp1.body(), Film.class).getId();

        HttpResponse<String> resp2 = testClient.getFilms("/" + filmId);
        Film respFilm = objectMapper.readValue(resp2.body(), Film.class);

        Assertions.assertEquals(OK, resp2.statusCode());
        Assertions.assertEquals(filmName, respFilm.getName());
    }

    @Test
    void getUserByIdShouldReturnCorrectUser() throws Exception {
        User firstUser = new User();
        firstUser.setEmail("sgvipbopibdfomrOPgo@mail");
        firstUser.setLogin("login");

        HttpResponse<String> resp1 = testClient.postUsers(firstUser);
        Long id = objectMapper.readValue(resp1.body(), User.class).getId();
        User exceptedUser = new User();
        exceptedUser.setId(id);
        exceptedUser.setLogin("login");
        exceptedUser.setEmail("sgvipbopibdfomrOPgo@mail");
        exceptedUser.setName("login");

        HttpResponse<String> resp2 = testClient.getUsers("/" + id);
        User actualUser = objectMapper.readValue(resp2.body(), User.class);
        Assertions.assertEquals(OK, resp2.statusCode());
        Assertions.assertEquals(exceptedUser, actualUser);
    }

    @Test
    void putUserIdFriendsIdShouldReturn204() throws Exception {
        User firstUser = new User();
        firstUser.setEmail("user@mail");
        firstUser.setLogin("login");

        User secondUser = new User();
        secondUser.setEmail("friend@mail");
        secondUser.setLogin("login");

        HttpResponse<String> resp1 = testClient.postUsers(firstUser);
        HttpResponse<String> resp2 = testClient.postUsers(secondUser);

        Long firstId = objectMapper.readValue(resp1.body(), User.class).getId();
        Long secondId = objectMapper.readValue(resp2.body(), User.class).getId();

        HttpResponse<String> resp3 = testClient.putUsers("/" + firstId + "/friends/" + secondId);
        Assertions.assertEquals(NO_CONTENT, resp3.statusCode());
    }

    @Test
    void putUserIdFriendsIdShouldReturn404IfIdIncorrect() throws Exception {
        HttpResponse<String> resp3 = testClient.putUsers("/" + -1 + "/friends/" + 2);
        HttpResponse<String> resp4 = testClient.putUsers("/" + 2 + "/friends/" + -1);
        Assertions.assertEquals(NOT_FOUND, resp3.statusCode());
        Assertions.assertEquals(NOT_FOUND, resp4.statusCode());
    }

    @Test
    void getUserIdFriendsShouldReturnCorrectListFriends() throws Exception {
        testClient.putUsers("/" + 1 + "/friends/" + 2);
        testClient.putUsers("/" + 1 + "/friends/" + 3);

        HttpResponse<String> resp4 = testClient.getUsers("/" + 1 + "/friends");
        Set<User> userRespSet = objectMapper.readValue(resp4.body(), new TypeReference<Set<User>>() {});
        Assertions.assertEquals(Set.of(userList.get(1), userList.get(2)), userRespSet);
        Assertions.assertEquals(OK, resp4.statusCode());
    }

    @Test
    void getUserIdFriendsIdShouldReturnCorrectListOfCommonFriends() throws Exception {
        testClient.putUsers("/" + 1 + "/friends/" + 2);
        testClient.putUsers("/" + 1 + "/friends/" + 3);
        testClient.putUsers("/" + 2 + "/friends/" + 3);

        HttpResponse<String> resp = testClient.getUsers("/" + 1 + "/friends/common/" + 2);
        Set<User> userRespSet = objectMapper.readValue(resp.body(), new TypeReference<Set<User>>() {});
        Assertions.assertEquals(userRespSet, Set.of(userList.get(2)));
        Assertions.assertEquals(OK, resp.statusCode());
    }

    @Test
    void deleteUserIdFriendsIdShouldReturn200() throws Exception {
        testClient.putUsers("/" + 1 + "/friends/" + 2);

        HttpResponse<String> resp4 = testClient.deleteUser("/" + 1 + "/friends/" + 2);
        Assertions.assertEquals(OK, resp4.statusCode());
    }

    @Test
    void LikeFilmShouldReturn200IfRequestCorrect() throws Exception {
        HttpResponse<String> resp = testClient.putFilms("/" + 1 + "/like/" + 1);
        Assertions.assertEquals(OK, resp.statusCode());
    }

    @Test
    void LikeFilmShouldReturn404IfFilmIdNotExist() throws Exception {
        HttpResponse<String> resp = testClient.putFilms("/" + 100 + "/like/" + 1);
        Assertions.assertEquals(NOT_FOUND, resp.statusCode());
    }

    @Test
    void LikeFilmShouldReturn404IfUserIdNotExist() throws Exception {
        HttpResponse<String> resp = testClient.putFilms("/" + 3 + "/like/" + 100);
        Assertions.assertEquals(NOT_FOUND, resp.statusCode());
    }

    @Test
    void deleteLikeReturn200IdRequestCorrect() throws Exception {
        testClient.putFilms("/" + 1 + "/like/" + 1);
        HttpResponse<String> resp = testClient.deleteFilms("/" + 1 + "/like/" + 1);
        Assertions.assertEquals(OK, resp.statusCode());
    }

    @Test
    void deleteLikeReturn404IfFilmIdNotExist() throws Exception {
        testClient.putFilms("/" + 1 + "/like/" + 1);
        HttpResponse<String> resp = testClient.deleteFilms("/" + 100 + "/like/" + 1);
        Assertions.assertEquals(NOT_FOUND, resp.statusCode());
    }

    @Test
    void deleteLikeReturn404IfUserIdNotExist() throws Exception {
        testClient.putFilms("/" + 1 + "/like/" + 1);
        HttpResponse<String> resp = testClient.deleteFilms("/" + 3 + "/like/" + 100);
        Assertions.assertEquals(NOT_FOUND, resp.statusCode());
    }

    @Test
    void getPopularFilmReturnTop3IfCountExist() throws Exception {
        testClient.putFilms("/" + 1 + "/like/" + 2);
        testClient.putFilms("/" + 1 + "/like/" + 3);
        testClient.putFilms("/" + 1 + "/like/" + 4);
        testClient.putFilms("/" + 2 + "/like/" + 3);
        testClient.putFilms("/" + 2 + "/like/" + 4);
        testClient.putFilms("/" + 3 + "/like/" + 4);

        HttpResponse<String> resp = testClient.getFilms("/popular?count=3");
        List<Film> filmRespList = objectMapper.readValue(resp.body(), new TypeReference<List<Film>>() {});
        Assertions.assertEquals(OK, resp.statusCode());
        List<Film> exceptedFilms = new ArrayList<>();
        exceptedFilms.add(filmList.get(0));
        exceptedFilms.add(filmList.get(1));
        exceptedFilms.add(filmList.get(2));

        Assertions.assertEquals(exceptedFilms, filmRespList);

    }

    @Test
    void getPopularFilmReturnTop3IfCountNotExist() throws Exception {
        testClient.putFilms("/" + 1 + "/like/" + 2);
        testClient.putFilms("/" + 1 + "/like/" + 3);
        testClient.putFilms("/" + 1 + "/like/" + 4);
        testClient.putFilms("/" + 2 + "/like/" + 3);
        testClient.putFilms("/" + 2 + "/like/" + 4);
        testClient.putFilms("/" + 3 + "/like/" + 4);

        HttpResponse<String> resp = testClient.getFilms("/popular");
        List<Film> filmRespList = objectMapper.readValue(resp.body(), new TypeReference<List<Film>>() {});
        Assertions.assertEquals(OK, resp.statusCode());
        List<Film> exceptedFilms = new ArrayList<>();
        exceptedFilms.add(filmList.get(0));
        exceptedFilms.add(filmList.get(1));
        exceptedFilms.add(filmList.get(2));

        Assertions.assertEquals(exceptedFilms, filmRespList);

    }









}
