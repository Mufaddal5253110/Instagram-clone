package my.insta.androrealm.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.insta.androrealm.R;
import my.insta.androrealm.Utils.Heart;
import my.insta.androrealm.Utils.SquareImageView;
import my.insta.androrealm.Utils.UniversalImageLoader;
import my.insta.androrealm.models.Comments;
import my.insta.androrealm.models.Likes;
import my.insta.androrealm.models.Photo;
import my.insta.androrealm.models.Users;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";

    public ViewPostFragment(){
        super();
        setArguments(new Bundle());
    }



    //widgets
    private SquareImageView mPostImage;
    private TextView mCaption, mUsername, mTimestamp,mTags,mLikes,mtotalComments;
    private ImageView mBackArrow, mComments, mHeartRed, mHeart, mProfileImage,moption,msend;
    String lcaption,ltags,lusername;
    private ProgressBar mProgressBar;

    //vars
    Photo mPhoto;
    private Heart mheart;
    Boolean mLikedByCurrentUser;
    StringBuilder mUsers;
    Users user;
    String mLikesString = "";

    private GestureDetector mGestureDetector;

    DatabaseReference databaseReference,ref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_posts_viewer, container, false);
        mPostImage = (SquareImageView) view.findViewById(R.id.post_imagee);
        mBackArrow = (ImageView) view.findViewById(R.id.back_from_post_viewer);
        mCaption = (TextView) view.findViewById(R.id.txt_caption);
        mTags = (TextView) view.findViewById(R.id.txt_tags);
        mUsername = (TextView) view.findViewById(R.id.username);
        mTimestamp = (TextView) view.findViewById(R.id.txt_timePosted);
        mtotalComments = (TextView) view.findViewById(R.id.txt_commments);
        mLikes = (TextView) view.findViewById(R.id.txt_likes);
        mComments = (ImageView) view.findViewById(R.id.img_comments);
        mHeartRed = (ImageView) view.findViewById(R.id.img_heart_red);
        mHeart = (ImageView) view.findViewById(R.id.img_heart);
        mProfileImage = (ImageView) view.findViewById(R.id.user_img);
        moption = (ImageView) view.findViewById(R.id.option);
        msend = (ImageView) view.findViewById(R.id.img_send);
        mProgressBar = (ProgressBar) view.findViewById(R.id.viewpostProgressBar);


        mheart = new Heart(mHeart, mHeartRed);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());



        try{
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_Path(), mPostImage, null, "");
            retrivingData();
            getLikesString();
//            UniversalImageLoader.setImage(getPhotoFromBundle().getImage_Path(), mPostImage, null, "");
//            String photo_id = getPhotoFromBundle().getPhoto_id();
//
//            Query query = FirebaseDatabase.getInstance().getReference()
//                    .child("Photo")
//                    .orderByChild("photo_id")
//                    .equalTo(photo_id);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//                        Photo newPhoto = new Photo();
//                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//
//                        newPhoto.setCaption(objectMap.get("caption").toString());
//                        newPhoto.setTags(objectMap.get("tags").toString());
//                        newPhoto.setPhoto_id(objectMap.get("photo_id").toString());
//                        newPhoto.setUser_id(objectMap.get("user_id").toString());
//                        newPhoto.setDate_Created(objectMap.get("date_Created").toString());
//                        newPhoto.setImage_Path(objectMap.get("image_Path").toString());
//
//                        List<Comments> commentsList = new ArrayList<Comments>();
//                        for (DataSnapshot dSnapshot : singleSnapshot
//                                .child("comments").getChildren()){
//                            Comments comment = new Comments();
//                            comment.setUser_id(dSnapshot.getValue(Comments.class).getUser_id());
//                            comment.setComment(dSnapshot.getValue(Comments.class).getComment());
//                            comment.setDate_created(dSnapshot.getValue(Comments.class).getDate_created());
//                            commentsList.add(comment);
//                        }
//                        newPhoto.setComments(commentsList);
//
//                        mPhoto = newPhoto;
//                        Log.d(TAG, "getPhotoFromBundle(mPhoto): arguments: " + mPhoto);
//
//                        retrivingData();
//                        getLikesString();
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("ViewPostFragment", "onCancelled: query cancelled.");
//                }
//            });

        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }

        return view;
    }


    private void getLikesString(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("Photo")
                .child(mPhoto.getPhoto_id())
                .child("likes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child("Users")
                            .orderByChild("user_id")
                            .equalTo(singleSnapshot.getValue(Likes.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                                mUsers.append(singleSnapshot.getValue(Users.class).getUsername());
                                mUsers.append(",");
                            }

                            String[] splitUsers = mUsers.toString().split(",");

                            if(mUsers.toString().contains(user.getUsername()+ ",")){
                                mLikedByCurrentUser = true;
                            }else{
                                mLikedByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            if(length == 1){
                                mLikesString = "Liked by " + splitUsers[0];

                            }
                            else if(length == 2){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + " and " + splitUsers[1];

                            }
                            else if(length == 3){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + splitUsers[2];

                            }
                            else if(length == 4){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + splitUsers[3];

                            }
                            else if(length > 4){
                                mLikesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + (splitUsers.length - 3) + " others";

                            }
                            setupWidgets();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(!dataSnapshot.exists()){
                    mLikesString = "";
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child("Photo")
                    .child(mPhoto.getPhoto_id())
                    .child("likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();

                        //case1: Then user already liked the photo
                        if(mLikedByCurrentUser &&
                                singleSnapshot.getValue(Likes.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            reference.child("Photo")
                                    .child(mPhoto.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();
///
                            reference.child("User_Photo")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mPhoto.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();

                            mheart.toggleLike();
                            getLikesString();
                        }
                        //case2: The user has not liked the photo
                        else if(!mLikedByCurrentUser){
                            //add new like
                            addNewLike();
                            break;
                        }
                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }
    private void addNewLike(){

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        String newLikeID = myRef.push().getKey();
        Likes like = new Likes();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.child("Photo")
                .child(mPhoto.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);

        myRef.child("User_Photo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mPhoto.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);

        mheart.toggleLike();
        getLikesString();
        addLikeNotification(mPhoto.getUser_id(),mPhoto.getPhoto_id());
    }

    /**
     * retrieve the photo from the incoming bundle from profilefragment
     * @return
     */
    private Photo getPhotoFromBundle(){

        Bundle bundle = this.getArguments();
        Log.d(TAG, "getPhotoFromBundle: arguments: " + bundle.getParcelable("PHOTO"));

        if(bundle != null) {
            return bundle.getParcelable("PHOTO");
        }else{
            return null;
        }
    }

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(){

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mPhoto.getDate_Created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
            difference = "0";
        }
        return difference;
    }

    private void setupWidgets(){
        String timestampDiff = getTimestampDifference();
        if(!timestampDiff.equals("0")){
            mTimestamp.setText(timestampDiff + " days ago");
        }else{
            mTimestamp.setText("Today");
        }
        mLikes.setText(mLikesString);
        mCaption.setText(mPhoto.getCaption());

        if(mPhoto.getComments().size() > 0){
            mtotalComments.setText("View all " + mPhoto.getComments().size() + " comments");
        }else{
            mtotalComments.setText("");
        }

        mtotalComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: navigating to comments through text");

                Intent b = new Intent(getActivity(), ViewComments.class);
                //Create the bundle
                Bundle bundle = new Bundle();
                //Add your data from getFactualResults method to bundle
                bundle.putParcelable("Photo", mPhoto);
                b.putExtra("commentcount",mPhoto.getComments().size());
                //Add the bundle to the intent
                b.putExtras(bundle);
                startActivity(b);

            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ViewPostFragment", "onClick: navigating to profile");

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ViewPostFragment", "onClick: navigating to comments through icon");

                Intent b = new Intent(getActivity(), ViewComments.class);
                //Create the bundle
                Bundle bundle = new Bundle();
                //Add your data from getFactualResults method to bundle
                bundle.putParcelable("Photo", mPhoto);

                //Add the bundle to the intent
                b.putExtras(bundle);
                b.putExtra("commentcount",mPhoto.getComments().size());
                startActivity(b);

            }
        });

        if(mLikedByCurrentUser){
            mHeart.setVisibility(View.GONE);
            mHeartRed.setVisibility(View.VISIBLE);
            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
        else{
            mHeart.setVisibility(View.VISIBLE);
            mHeartRed.setVisibility(View.GONE);
            mHeart.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
    }

    private void retrivingData(){

        // Retriving data
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        ref = FirebaseDatabase.getInstance().getReference("User_Photo").child(userid).child(mPhoto.getPhoto_id());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(Users.class);
                Glide.with(ViewPostFragment.this)
                        .load(user.getProfilePhoto())
                        .into(mProfileImage);

                lcaption = mPhoto.getCaption();
                ltags = mPhoto.getTags();
                lusername = user.getUsername();

                mTags.setText(ltags);
                mUsername.setText(lusername);
//                mCaption.setText(lusername+" "+lcaption);
                mProgressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void onResume() {

        super.onResume();
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    getActivity().getSupportFragmentManager().popBackStack();
                }
                return true;
            }
        });
    }

    private void addLikeNotification(String userid,String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications");

        HashMap<String, Object> hashMappp = new HashMap<>();
        hashMappp.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMappp.put("postid", postid);
        hashMappp.put("text", "liked your post");
        hashMappp.put("ispost", true);
        reference.child(userid).push().setValue(hashMappp);

    }


}
