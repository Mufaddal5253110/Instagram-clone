package my.insta.androrealm.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import my.insta.androrealm.Post.PostActivity;

public class methods {

    //******************************METHOD TO GET POST COUNT***********************
    public int getImagecount(DataSnapshot dataSnapshot){
        int count = 0;
        for(DataSnapshot ds : dataSnapshot.child("User_Photo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }
}
