package com.lobox.imdblobox.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RatingDto implements Serializable {
    private String tconst;
    private String  averageRating;
    private String numVotes;
}
