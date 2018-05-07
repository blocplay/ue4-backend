package com.tokenplay.ue4.steam.client.types.api;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ApiError implements Serializable {

    public ApiError(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The code. */
    private String code;

    /** The description. */
    private String description;

    /** The message. */
    private String message;

    /** The errors. */
    private List<ApiError> errors;
}
