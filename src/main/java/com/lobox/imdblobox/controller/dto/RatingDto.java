package com.lobox.imdblobox.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto implements Serializable {
    private String tconst;
    private String averageRating;
    private String numVotes;
}
