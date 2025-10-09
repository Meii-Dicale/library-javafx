package cda.bibliotheque.model;

public class PhysicalState {
    private int id;
    private String state_name;

    public PhysicalState() {
    }

    public PhysicalState(int id, String state_name) {
        this.id = id;
        this.state_name = state_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStateName() {
        return state_name;
    }

    public void setStateName(String state_name) {
        this.state_name = state_name;
    }
}