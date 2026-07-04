package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class Film {

    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
}
