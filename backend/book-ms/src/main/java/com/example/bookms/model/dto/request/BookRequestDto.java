package com.example.bookms.model.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDto {
    @NotNull(message = "Name must be not null")
    String name;

    @NotNull(message = "Name must be not null")
    String author;

    LocalDate publishDate;
    int pageCount;
    double price;

    String categoryName;
}
