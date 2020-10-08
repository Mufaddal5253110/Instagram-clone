package my.insta.androrealm.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.insta.androrealm.R;
import my.insta.androrealm.Utils.GridImageAdapter;
import my.insta.androrealm.models.Comments;
import my.insta.androrealm.models.Likes;
import my.insta.androrealm.models.Photo;
import my.insta.androrealm.models.Users;

public class ProfileFragment extends Fragment {

    private static final int NUM_GRID_COLUMNS = 3;
    private static final String TAG ="ProfileFragment" ;

    ImageView account_setting_menu;
    Button editProfile;
    ImageView profilePhoto;
    GridView gridView;
    TextView posts,followers,followings,name, description,website,username;
    LinearLayout follower,following;
    String noFollowers,noFollowings;
    DatabaseReference databaseReference;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile,null);

        account_setting_menu = (ImageView) v.findViewById(R.id.account_settingMenu);
        editProfile = (Button)v.findViewById(R.id.edit_profile);
        profilePhoto = (ImageView)v.findViewById(R.id.user_img);
        gridView = (GridView) v.findViewById(R.id.gridview1);
        posts = (TextView)v.findViewById(R.id.txtPosts);
        followers = (TextView)v.findViewById(R.id.txtFollowers);
        followings = (TextView)v.findViewById(R.id.txtFollowing);
        name = (TextView)v.findViewById(R.id.display_name);
        description = (TextView)v.findViewById(R.id.description);
        website = (TextView)v.findViewById(R.id.website);
        username = (TextView)v.findViewById(R.id.profileName);
        follower = (LinearLayout)v.findViewById(R.id.FragmentProfile_followerLinearLayout);
        following = (LinearLayout)v.findViewById(R.id.FragmentProfile_followingLinearLayout);
        mProgressBar = (ProgressBar) v.findViewById(R.id.profileProgressBar);

        // Retriving Photos and displaying in profile
        tempGridSetup();

        // Retriving data
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.keepSynced(true);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Users user = snapshot.getValue(Users.class);
                posts.setText(user.getPosts());
                noFollowers = user.getFollowers();
                noFollowings = user.getFollowing();
                followers.setText(noFollowers);
                followings.setText(noFollowings);
                name.setText(user.getFullName());
                description.setText(user.getDiscription());
                website.setText(user.getWebsite());
                username.setText(user.getUsername());
                Glide.with(ProfileFragment.this)
                        .load(user.getProfilePhoto())
                        .into(profilePhoto);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        account_setting_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),Account_Settings.class);
                startActivity(intent);
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),EditProfile.class);
                startActivity(intent);
            }
        });

        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(),FollowersFollowing.class);
                intent.putExtra("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("title","Followers");
                intent.putExtra("number",noFollowers);
                startActivity(intent);

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),FollowersFollowing.class);
                intent.putExtra("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("title","Following");
                intent.putExtra("number",noFollowings);
                startActivity(intent);

            }
        });



        return v;
    }

    private void tempGridSetup(){
        Log.d(TAG, "setupGridView: Setting up image grid.");
        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        Query query = reference
                .child("User_Photo")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot singleSnapshot :  snapshot.getChildren()){
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    Log.d(TAG, "setupGridView(objectMap)"+objectMap.get("caption"));


                    photo.setCaption(objectMap.get("caption").toString());
                    photo.setTags(objectMap.get("tags").toString());
                    photo.setPhoto_id(objectMap.get("photo_id").toString());
                    photo.setUser_id(objectMap.get("user_id").toString());
                    photo.setDate_Created(objectMap.get("date_Created").toString());
                    photo.setImage_Path(objectMap.get("image_Path").toString());

                    List<Comments> comments = new ArrayList<Comments>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child("comments").getChildren()){
                        Comments comment = new Comments();
                        comment.setUser_id(dSnapshot.getValue(Comments.class).getUser_id());
                        comment.setComment(dSnapshot.getValue(Comments.class).getComment());
                        comment.setDate_created(dSnapshot.getValue(Comments.class).getDate_created());
                        comments.add(comment);
                    }

                    photo.setComments(comments);

                    List<Likes> likesList = new ArrayList<Likes>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child("likes").getChildren()){
                        Likes like = new Likes();
                        like.setUser_id(dSnapshot.getValue(Likes.class).getUser_id());
                        likesList.add(like);
                    }
                    photo.setLikes(likesList);
                    photos.add(photo);
//                    photos.add(singleSnapshot.getValue(Photo.class));
                }
                //setup our image grid
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                gridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls = new ArrayList<String>();
                for(int i = 0; i < photos.size(); i++){
                    imgUrls.add(photos.get(i).getImage_Path());
                }
                GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,
                        "", imgUrls);
                gridView.setAdapter(adapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        ViewPostFragment fragment = new ViewPostFragment();
                        Bundle args = new Bundle();
                        args.putParcelable("PHOTO", photos.get(position));
                        Log.d(TAG, "getPhotoFromBundle(PHOTO): arguments: " + photos.get(position));

                        fragment.setArguments(args);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(ProfileFragment.this.getId(), fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: query cancelled.");

            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    return true;
                }
                return false;
            }
        });
    }


}
