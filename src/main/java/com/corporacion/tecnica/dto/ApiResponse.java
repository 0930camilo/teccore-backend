package com.corporacion.tecnica.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final int status;
    private final String message;
    private final T data;
}

