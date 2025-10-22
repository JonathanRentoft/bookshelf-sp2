package app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDTO {
    private String error;

    public ErrorDTO() {}

    public ErrorDTO(String error) {
        this.error = error;
    }
}