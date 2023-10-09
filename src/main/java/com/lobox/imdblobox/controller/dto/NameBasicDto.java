package com.lobox.imdblobox.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class NameBasicDto implements Serializable {
    private String nConst;
    private String primaryName;
    private String birthYear;
    private String deathYear;
    private String primaryProfession;
    private String knownForTitles;
}
