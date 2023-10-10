package com.lobox.imdblobox.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RequestCountDto implements Serializable {
    private long requestCount;
}
