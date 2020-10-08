package my.insta.androrealm.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.insta.androrealm.Home;
import my.insta.androrealm.Messages.ChatActivity;
import my.insta.androrealm.R;
import my.insta.androrealm.Stories.StoryAdapter;
import my.insta.androrealm.Utils.HomeFragmentPostViewListAdapter;
import my.insta.androrealm.Utils.UniversalImageLoader;
import my.insta.androrealm.models.Comments;
import my.insta.androrealm.models.Photo;
import my.insta.androrealm.models.Story;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    //vars
    private ArrayList<Photo> mPhotos;
    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private HomeFragmentPostViewListAdapter mAdapter;
    private int mResults;
    ImageView message;


    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,null);
        mListView = v.findViewById(R.id.FragmentHome_postListView);
        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();
        mPaginatedPhotos = new ArrayList<>();

        message = v.findViewById(R.id.FragmentHome_msg);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });

        recyclerView_story = v.findViewById(R.id.FragmentHome_story_recyclerView);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(linearlayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(),storyList);
        recyclerView_story.setAdapter(storyAdapter);


        getFollowing();
        initImageLoader();
        displayMorePhotos();


        return v;
    }
    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("Following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child("user_id").getValue());

                    mFollowing.add(singleSnapshot.child("user_id").getValue().toString());
                }
                mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //get the photos
                readStory();
                getPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = reference
                    .child("User_Photo")
                    .child(mFollowing.get(i))
                    .orderByChild("user_id")
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get("caption").toString());
                        photo.setTags(objectMap.get("tags").toString());
                        photo.setPhoto_id(objectMap.get("photo_id").toString());
                        photo.setUser_id(objectMap.get("user_id").toString());
                        photo.setDate_Created(objectMap.get("date_Created").toString());
                        photo.setImage_Path(objectMap.get("image_Path").toString());


                        ArrayList<Comments> comments = new ArrayList<Comments>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child("comments").getChildren()){
                            Comments comment = new Comments();
                            comment.setUser_id(dSnapshot.getValue(Comments.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comments.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comments.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);
                        mPhotos.add(photo);
                    }
                    if(count >= mFollowing.size() -1){
                        //display our photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayPhotos(){
        if(mPhotos != null){
            try{
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_Created().compareTo(o1.getDate_Created());
                    }
                });

                int iterations = mPhotos.size();

                if(iterations > 10){
                    iterations = 10;
                }

                mResults = 10;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new HomeFragmentPostViewListAdapter(getActivity(), R.layout.fragment_home_post_viewer, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(mPhotos.size() > mResults && mPhotos.size() > 0){

                int iterations;
                if(mPhotos.size() > (mResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - mResults;
                }

                //add the new photos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", "",
                        FirebaseAuth.getInstance().getCurrentUser().getUid(), 0,0));
                for (String id : mFollowing) {
                    int countStory = 0;
                    Story story = null;
                    if(id != FirebaseAuth.getInstance().getCurrentUser().getUid()){

                        for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
                            story = snapshot.getValue(Story.class);
                            if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                                countStory++;
                            }
                        }
                        if (countStory > 0){
                            storyList.add(story);
                        }

                    }
                }

                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
