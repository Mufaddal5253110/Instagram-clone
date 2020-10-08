package my.insta.androrealm.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Photo implements Parcelable {

    private String Caption,Date_Created,Image_Path,Photo_id,User_id,Tags;
    private List<Likes> likes;
    private List<Comments> comments;

    public Photo(String caption, String date_Created, String image_Path, String photo_id, String user_id, String tags, List<Likes> likes, List<Comments> comments) {
        Caption = caption;
        Date_Created = date_Created;
        Image_Path = image_Path;
        Photo_id = photo_id;
        User_id = user_id;
        Tags = tags;
        this.likes = likes;
        this.comments = comments;
    }

    public Photo(){
    }


    protected Photo(Parcel in) {
        Caption = in.readString();
        Date_Created = in.readString();
        Image_Path = in.readString();
        Photo_id = in.readString();
        User_id = in.readString();
        Tags = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getDate_Created() {
        return Date_Created;
    }

    public void setDate_Created(String date_Created) {
        Date_Created = date_Created;
    }

    public String getImage_Path() {
        return Image_Path;
    }

    public void setImage_Path(String image_Path) {
        Image_Path = image_Path;
    }

    public String getPhoto_id() {
        return Photo_id;
    }

    public void setPhoto_id(String photo_id) {
        Photo_id = photo_id;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public String getTags() {
        return Tags;
    }

    public void setTags(String tags) {
        Tags = tags;
    }

    public List<Likes> getLikes() {
        return likes;
    }

    public void setLikes(List<Likes> likes) {
        this.likes = likes;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "Caption='" + Caption + '\'' +
                ", Date_Created='" + Date_Created + '\'' +
                ", Image_Path='" + Image_Path + '\'' +
                ", Photo_id='" + Photo_id + '\'' +
                ", User_id='" + User_id + '\'' +
                ", Tags='" + Tags + '\'' +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Caption);
        dest.writeString(Date_Created);
        dest.writeString(Image_Path);
        dest.writeString(Photo_id);
        dest.writeString(User_id);
        dest.writeString(Tags);
    }
}
