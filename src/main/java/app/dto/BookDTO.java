package app.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDTO {
    private Long id;
    private String title;
    private String author;

    public BookDTO() {}

    public BookDTO(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public BookDTO(String title, String author) {
        this.title = title;
        this.author = author;
    }
}