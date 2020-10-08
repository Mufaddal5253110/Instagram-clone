package my.insta.androrealm.models;

public class privatedetails {

    private String user_id,Email,Gender,Birthdate,PhoneNumber;

    public privatedetails(){
    }

    public privatedetails(String user_id, String email, String gender, String birthdate, String phoneNumber) {
        this.user_id = user_id;
        Email = email;
        Gender = gender;
        Birthdate = birthdate;
        PhoneNumber = phoneNumber;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(String birthdate) {
        Birthdate = birthdate;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "privatedetails{" +
                "user_id='" + user_id + '\'' +
                ", Email='" + Email + '\'' +
                ", Gender='" + Gender + '\'' +
                ", Birthdate='" + Birthdate + '\'' +
                ", PhoneNumber=" + PhoneNumber +
                '}';
    }
}
