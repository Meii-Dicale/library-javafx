package cda.bibliotheque.model;

import java.util.ArrayList;
import java.util.List;

public class Media {
    private int id;
    private String title;
    private String edition;
    private int year;
    private String summary;
    private List<Category> categories = new ArrayList<>();
    private Author author;

    public Media() {
    }

    public Media(String title, String edition, int year, String summary, int author_id) {
        this.title = title;
        this.edition = edition;
        this.year = year;
        this.summary = summary;
        // Note: This constructor is not ideal as it uses author_id.
        // It's better to pass the full Author object.
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}