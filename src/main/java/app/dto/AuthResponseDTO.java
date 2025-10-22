package app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    private String username;
    private String token;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String username, String token) {
        this.username = username;
        this.token = token;
    }
}