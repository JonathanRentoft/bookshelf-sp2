package app.dto;

public class ErrorDTO {
    private String error;

    public ErrorDTO() {}

    public ErrorDTO(String error) {
        this.error = error;
    }

    // Getters and Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}