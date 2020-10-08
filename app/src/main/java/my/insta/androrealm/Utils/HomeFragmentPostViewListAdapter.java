package my.insta.androrealm.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import my.insta.androrealm.Profile.ViewComments;
import my.insta.androrealm.R;
import my.insta.androrealm.Search.UserSearchViewPost;
import my.insta.androrealm.models.Comments;
import my.insta.androrealm.models.Likes;
import my.insta.androrealm.models.Photo;
import my.insta.androrealm.models.Users;
import my.insta.androrealm.models.privatedetails;

public class HomeFragmentPostViewListAdapter extends ArrayAdapter<Photo> {

    public interface OnLoadMoreItemsListener{
        void onLoadMoreItems();
    }
    OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private static final String TAG = "HomePostViewListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername = "";
    private ProgressBar mProgressBar;


    public HomeFragmentPostViewListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;
        mReference = FirebaseDatabase.getInstance().getReference();
    }
    static class ViewHolder{
        CircleImageView mprofileImage;
        String likesString = "";
        TextView username, timeDetla, caption, likes, comments,mTags;
        SquareImageView image;
        ImageView heartRed, heartWhite, comment;

        Users settings = new Users();
//        privatedetails user  = new privatedetails();
        StringBuilder users;
//        String mLikesString;
        boolean likeByCurrentUser;
        Heart heart;
        GestureDetector detector;
        Photo photo;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_username);
            holder.image = (SquareImageView) convertView.findViewById(R.id.fragment_home_post_viewer_post_image);
            holder.heartRed = (ImageView) convertView.findViewById(R.id.fragment_home_post_viewer_img_heart_red);
            holder.heartWhite = (ImageView) convertView.findViewById(R.id.fragment_home_post_viewer_img_heart);
            holder.comment = (ImageView) convertView.findViewById(R.id.fragment_home_post_viewer_img_comments);
            holder.likes = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_txt_likes);
            holder.comments = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_txt_commments);
            holder.caption = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_txt_caption);
            holder.timeDetla = (TextView) convertView.findViewById(R.id.fragment_home_post_viewer_txt_timePosted);
            holder.mprofileImage = (CircleImageView) convertView.findViewById(R.id.fragment_home_post_viewer_user_img);
            holder.mTags = (TextView)convertView.findViewById(R.id.fragment_home_post_viewer_txt_tags);
            holder.heart = new Heart(holder.heartWhite, holder.heartRed);
            holder.photo = getItem(position);
            holder.detector = new GestureDetector(mContext, new GestureListener(holder));
            holder.users = new StringBuilder();
//            mProgressBar = (ProgressBar)convertView.findViewById(R.id.fragment_home_post_viewer_ProgressBar);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //get the current users username (need for checking likes string)
        getCurrentUsername();

        //get likes string
        getLikesString(holder);

        //set the caption
        holder.caption.setText(getItem(position).getCaption());

        //set Tags
        holder.mTags.setText(getItem(position).getTags());

        //set the comment
        final List<Comments> comments = getItem(position).getComments();
        holder.comments.setText("View all " + comments.size() + " comments");
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: loading comment thread for " + getItem(position).getPhoto_id());
                Intent b = new Intent(mContext, ViewComments.class);
                //Create the bundle
                Bundle bundle = new Bundle();
                //Add your data from getFactualResults method to bundle
                bundle.putParcelable("Photo", getItem(position));
                b.putExtra("commentcount",comments.size());
                //Add the bundle to the intent
                b.putExtras(bundle);
                mContext.startActivity(b);

            }
        });

        //set the time it was posted
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timeDetla.setText(timestampDifference + " DAYS AGO");
        }else{
            holder.timeDetla.setText("TODAY");
        }

        //set the profile image
        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImage_Path(), holder.image);


        //get the profile image and username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("Users")
                .orderByChild("user_id")
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    // currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();

                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot.getValue(Users.class).getUsername());

                    holder.username.setText(singleSnapshot.getValue(Users.class).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    holder.username);

//                            Intent intent = new Intent(mContext, ProfileActivity.class);
//                            intent.putExtra(mContext.getString(R.string.calling_activity),
//                                    mContext.getString(R.string.home_activity));
//                            intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
//                            mContext.startActivity(intent);
                        }
                    });

                    imageLoader.displayImage(singleSnapshot.getValue(Users.class).getProfilePhoto(),
                            holder.mprofileImage);
                    holder.mprofileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    holder.username);

//                            Intent intent = new Intent(mContext, ProfileActivity.class);
//                            intent.putExtra(mContext.getString(R.string.calling_activity),
//                                    mContext.getString(R.string.home_activity));
//                            intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
//                            mContext.startActivity(intent);
                        }
                    });



                    holder.settings = singleSnapshot.getValue(Users.class);
                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent b = new Intent(mContext, ViewComments.class);
                            //Create the bundle
                            Bundle bundle = new Bundle();
                            //Add your data from getFactualResults method to bundle
                            bundle.putParcelable("Photo", getItem(position));
                            b.putExtra("commentcount",comments.size());
                            //Add the bundle to the intent
                            b.putExtras(bundle);
                            mContext.startActivity(b);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the user object
//        Query userQuery = mReference
//                .child("Users")
//                .orderByChild("user_id")
//                .equalTo(getItem(position).getUser_id());
//        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    Log.d(TAG, "onDataChange: found user: " +
//                            singleSnapshot.getValue(Users.class).getUsername());
//
////                    holder.user = singleSnapshot.getValue(privatedetails.class);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        if(reachedEndOfList(position)){
            loadMoreData();
        }

        return convertView;
    }
    private boolean reachedEndOfList(int position){
        return position == getCount() - 1;
    }
    private void loadMoreData(){

        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) getContext();
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }
    public class GestureListener extends GestureDetector.SimpleOnGestureListener{

        ViewHolder mHolder;
        public GestureListener(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d(TAG, "onSingleTapConfirmed: Singletap detected.");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child("Photo")
                    .child(mHolder.photo.getPhoto_id())
                    .child("likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();

                        //case1: Then user already liked the photo
                        if(mHolder.likeByCurrentUser &&
                                singleSnapshot.getValue(Likes.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            mReference.child("Photo")
                                    .child(mHolder.photo.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();
///
                            mReference.child("User_Photo")
                                    .child(mHolder.photo.getUser_id())
                                    .child(mHolder.photo.getPhoto_id())
                                    .child("likes")
                                    .child(keyID)
                                    .removeValue();

                            mHolder.heart.toggleLike();
                            getLikesString(mHolder);
                        }
                        //case2: The user has not liked the photo
                        else if(!mHolder.likeByCurrentUser){
                            //add new like
                            addNewLike(mHolder);
                            break;
                        }
                    }
                    if(!dataSnapshot.exists()){
                        //add new like
                        addNewLike(mHolder);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
    }
    private void addNewLike(final ViewHolder holder){
        Log.d(TAG, "addNewLike: adding new like");

        String newLikeID = mReference.push().getKey();
        Likes like = new Likes();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mReference.child("Photo")
                .child(holder.photo.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);

        mReference.child("User_Photo")
                .child(holder.photo.getUser_id())
                .child(holder.photo.getPhoto_id())
                .child("likes")
                .child(newLikeID)
                .setValue(like);

        holder.heart.toggleLike();
        getLikesString(holder);
        addLikeNotification(holder.photo.getUser_id(),holder.photo.getPhoto_id());

    }
    private void getCurrentUsername(){
        Log.d(TAG, "getCurrentUsername: retrieving users");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("Users")
                .orderByChild("user_id")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currentUsername = singleSnapshot.getValue(Users.class).getUsername();
                }
//                getLikesString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "getCurrentUsername: query cancelled.");

            }
        });
    }
    private void getLikesString(final ViewHolder holder){
        Log.d(TAG, "getLikesString: getting likes string");

        try{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child("Photo")
                    .child(holder.photo.getPhoto_id())
                    .child("likes");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.users = new StringBuilder();
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
                                    Log.d(TAG, "onDataChange: found like: " +
                                            singleSnapshot.getValue(Users.class).getUsername());

                                    holder.users.append(singleSnapshot.getValue(Users.class).getUsername());
                                    holder.users.append(",");
                                }

                                String[] splitUsers = holder.users.toString().split(",");
                                Log.d(TAG, "onDataChange: getLikesString: holder.users.toString():" +
                                        holder.users.toString());
                                Log.d(TAG, "onDataChange: getLikesString: currentUsername:" +
                                        currentUsername);


                                if(holder.users.toString().contains(currentUsername + ",")){
                                    holder.likeByCurrentUser = true;
                                }else{
                                    holder.likeByCurrentUser = false;
                                }

                                int length = splitUsers.length;
                                if(length == 1){
                                    holder.likesString = "Liked by " + splitUsers[0];
                                }
                                else if(length == 2){
                                    holder.likesString = "Liked by " + splitUsers[0]
                                            + " and " + splitUsers[1];
                                }
                                else if(length == 3){
                                    holder.likesString = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + " and " + splitUsers[2];

                                }
                                else if(length == 4){
                                    holder.likesString = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + ", " + splitUsers[2]
                                            + " and " + splitUsers[3];
                                }
                                else if(length > 4){
                                    holder.likesString = "Liked by " + splitUsers[0]
                                            + ", " + splitUsers[1]
                                            + ", " + splitUsers[2]
                                            + " and " + (splitUsers.length - 3) + " others";
                                }
                                Log.d(TAG, "onDataChange: likes string: " + holder.likesString);
                                //setup likes string
                                setupLikesString(holder, holder.likesString);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    if(!dataSnapshot.exists()){
                        holder.likesString = "";
                        holder.likeByCurrentUser = false;
                        //setup likes string
                        setupLikesString(holder, holder.likesString);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e){
            Log.e(TAG, "getLikesString: NullPointerException: " + e.getMessage() );
            holder.likesString = "";
            holder.likeByCurrentUser = false;
            //setup likes string
            setupLikesString(holder, holder.likesString);
        }
    }
    private void setupLikesString(final ViewHolder holder, String likesString){
        Log.d(TAG, "setupLikesString: likes string:" + holder.likesString);

        if(holder.likeByCurrentUser){
            Log.d(TAG, "setupLikesString: photo is liked by current user");
            holder.heartWhite.setVisibility(View.GONE);
            holder.heartRed.setVisibility(View.VISIBLE);
            holder.heartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        }else{
            Log.d(TAG, "setupLikesString: photo is not liked by current user");
            holder.heartWhite.setVisibility(View.VISIBLE);
            holder.heartRed.setVisibility(View.GONE);
            holder.heartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        }
        holder.likes.setText(likesString);
//        mProgressBar.setVisibility(View.GONE);

    }
    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(Photo photo){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photo.getDate_Created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
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
