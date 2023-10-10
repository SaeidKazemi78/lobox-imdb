package com.lobox.imdblobox.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrincipalDto implements Serializable {
    private String tconst;
    private String ordering;
    private String nconst;
    private String category;
    private String job;
    private String characters;
}
