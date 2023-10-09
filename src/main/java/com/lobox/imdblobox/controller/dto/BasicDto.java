package com.lobox.imdblobox.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BasicDto implements Serializable {
//tconst	titleType	primaryTitle	originalTitle	isAdult	startYear	endYear	runtimeMinutes	genres
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
