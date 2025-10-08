package cda.bibliotheque.model;

public class Media {
    private int id ;
    private String title;
    private String edition;
    private int year;
    private String summary;
    private int author_id;

    public Media(){};

    public Media(String title, String edition, int year, String summary, int author_id){
        this.title = title;
        this.edition = edition;
        this.year = year;
        this.summary = summary;
        this.author_id = author_id;

    }
    public int getId(){
        return this.id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getEdition(){
        return this.edition;
    }
    public void setEdition(String edition){
        this.edition = edition;
    }
    public int getYear(){
        return this.year;
    }
    public void setYear(int year){
        this.year = year;
    }
    public String getSummary(){
        return this.summary;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }
    public int getAuthor_id(){
        return this.author_id;
    }
    public void setAuthor_id(int author_id){
        this.author_id = author_id;
    }   
}
