package com.lobox.imdblobox.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrewDto {
    private String tConst;
    private String director;
    private String writer;
}
