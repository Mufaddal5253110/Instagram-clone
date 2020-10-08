package my.insta.androrealm.models;

public class Likes {

    private String user_id;

    public Likes(String user_id) {
        this.user_id = user_id;
    }
    public Likes(){

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Likes{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
