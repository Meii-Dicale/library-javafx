package cda.bibliotheque.model;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private LocalDate started_at_date;
    private LocalDate ended_at_date;
    private boolean is_ended;
    private User user;
    private Stock stock;

    public Reservation() {
    }

    public Reservation(int id, LocalDate started_at_date, LocalDate ended_at_date, boolean is_ended, User user, Stock stock) {
        this.id = id;
        this.started_at_date = started_at_date;
        this.ended_at_date = ended_at_date;
        this.is_ended = is_ended;
        this.user = user;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartedAtDate() {
        return started_at_date;
    }

    public void setStartedAtDate(LocalDate started_at_date) {
        this.started_at_date = started_at_date;
    }

    public LocalDate getEndedAtDate() {
        return ended_at_date;
    }

    public void setEndedAtDate(LocalDate ended_at_date) {
        this.ended_at_date = ended_at_date;
    }

    public boolean isEnded() {
        return is_ended;
    }

    public void setEnded(boolean ended) {
        is_ended = ended;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}