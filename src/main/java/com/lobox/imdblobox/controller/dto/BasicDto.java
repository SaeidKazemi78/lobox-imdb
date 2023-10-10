package com.lobox.imdblobox.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BasicDto implements Serializable {
    private String tconst;
    private String titleType;
    private String primaryTitle;
    private String originalTitle;
    private String isAdult;
    private String startYear;
    private String endYear;
    private String runtimeMinutes;
    private String genres;
}
