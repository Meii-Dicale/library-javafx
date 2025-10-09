package cda.bibliotheque.model;

public class Stock {
    private int id;
    private boolean is_available;
    private Media media;
    private PhysicalState physicalState;

    public Stock() {
    }

    public Stock(int id, boolean is_available, Media media, PhysicalState physicalState) {
        this.id = id;
        this.is_available = is_available;
        this.media = media;
        this.physicalState = physicalState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return is_available;
    }

    public void setAvailable(boolean available) {
        is_available = available;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public PhysicalState getPhysicalState() {
        return physicalState;
    }

    public void setPhysicalState(PhysicalState physicalState) {
        this.physicalState = physicalState;
    }
}