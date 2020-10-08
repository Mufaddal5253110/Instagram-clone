package my.insta.androrealm.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Users implements Parcelable {

    private String Discription, Followers, Following, FullName, Posts, ProfilePhoto, Username, Website,User_id;

    public Users() {
    }

    public Users(String discription, String followers, String following, String fullName, String posts, String profilePhoto, String username, String website, String user_id) {
        Discription = discription;
        Followers = followers;
        Following = following;
        FullName = fullName;
        Posts = posts;
        ProfilePhoto = profilePhoto;
        Username = username;
        Website = website;
        User_id = user_id;
    }

    protected Users(Parcel in) {
        Discription = in.readString();
        Followers = in.readString();
        Following = in.readString();
        FullName = in.readString();
        Posts = in.readString();
        ProfilePhoto = in.readString();
        Username = in.readString();
        Website = in.readString();
        User_id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Discription);
        dest.writeString(Followers);
        dest.writeString(Following);
        dest.writeString(FullName);
        dest.writeString(Posts);
        dest.writeString(ProfilePhoto);
        dest.writeString(Username);
        dest.writeString(Website);
        dest.writeString(User_id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public String getDiscription() {
        return Discription;
    }

    public void setDiscription(String discription) {
        Discription = discription;
    }

    public String getFollowers() {
        return Followers;
    }

    public void setFollowers(String followers) {
        Followers = followers;
    }

    public String getFollowing() {
        return Following;
    }

    public void setFollowing(String following) {
        Following = following;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPosts() {
        return Posts;
    }

    public void setPosts(String posts) {
        Posts = posts;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        ProfilePhoto = profilePhoto;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    @Override
    public String toString() {
        return "Users{" +
                "Discription='" + Discription + '\'' +
                ", Followers='" + Followers + '\'' +
                ", Following='" + Following + '\'' +
                ", FullName='" + FullName + '\'' +
                ", Posts='" + Posts + '\'' +
                ", ProfilePhoto='" + ProfilePhoto + '\'' +
                ", Username='" + Username + '\'' +
                ", Website='" + Website + '\'' +
                ", User_id='" + User_id + '\'' +
                '}';
    }
}