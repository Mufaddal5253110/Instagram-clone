package my.insta.androrealm.models;

public class Passwords {

    private String Password;

    public Passwords(String password) {
        Password = password;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    @Override
    public String toString() {
        return "Passwords{" +
                "Password='" + Password + '\'' +
                '}';
    }
    public Passwords(){
    }
}
