package cda.bibliotheque.model;

public class User {
    private int id;
    private String user_name;
    private String password;
    private Boolean is_admin;
    private String mail;
    private int phone_number;

    public User(){

    }

    public User( String user_name, Boolean is_admin, String mail, int phone_number){
        this.user_name = user_name;
        this.is_admin = is_admin;
        this.mail = mail;
        this.phone_number = phone_number;
    }

    public User(String user_name, String password, Boolean is_admin, String mail, int phone_number) {
        this.user_name = user_name;
        this.password = password;
        this.is_admin = is_admin;
        this.mail = mail;
        this.phone_number = phone_number;
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getUser_name(){
        return user_name;
    }
    public void setUser_name(String user_name){
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public Boolean getIs_admin(){
        return is_admin;
    }
    public void setIs_admin(Boolean is_admin){
        this.is_admin = is_admin;
    }
    public String getMail(){
        return mail;
    }
    public void setMail(String mail){
        this.mail = mail;
    }
    public int getPhone_number(){
        return phone_number;
    }
    public void setPhone_number(int phone_number){
        this.phone_number = phone_number;
    }

}
