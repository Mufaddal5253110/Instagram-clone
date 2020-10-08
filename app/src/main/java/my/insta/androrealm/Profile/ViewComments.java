package my.insta.androrealm.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import my.insta.androrealm.R;
import my.insta.androrealm.Utils.CommentListAdapter;
import my.insta.androrealm.Utils.UniversalImageLoader;
import my.insta.androrealm.models.Comments;
import my.insta.androrealm.models.Likes;
import my.insta.androrealm.models.Photo;
import my.insta.androrealm.models.Users;


public class ViewComments extends AppCompatActivity {

    private static final String TAG ="ViewComments" ;


    //widgets
    private ImageView mBackArrow;
    private EditText mComment;
    private ListView mListView;
    private TextView mpost;
    ImageView profileImage;

    //vars
    Photo mphoto;
    ArrayList<Comments> mComments;
    Integer commentCount;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
        mBackArrow = (ImageView) findViewById(R.id.back_from_view_comment);
        mComment = (EditText) findViewById(R.id.comment);
        mListView = (ListView) findViewById(R.id.listView);
        mpost = (TextView)findViewById(R.id.post_comment) ;
        profileImage = (ImageView)findViewById(R.id.user_img);
        mComments = new ArrayList<>();

        try{
            mphoto = getPhotoFromBundle();
            commentCount = getIntent().getIntExtra("commentcount",0);
            Log.d(TAG, "getPhotoFromBundle: arguments: " + mphoto);

            getCommentList();


        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }
        // Retrieving profile photo
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Users user = snapshot.getValue(Users.class);
                Glide.with(ViewComments.this)
                        .load(user.getProfilePhoto())
                        .into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private Photo getPhotoFromBundle() {

        Bundle bundle = getIntent().getExtras();
//        Log.d(TAG, "getPhotoFromBundle: arguments: " + bundle.getParcelable("Photo"));
        if(bundle != null) {
            return bundle.getParcelable("Photo");
        }else{
            return null;
        }

    }
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addNewComment(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);

        String commentID = myRef.push().getKey();

        Comments comment = new Comments();
        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //insert into photos node
        myRef.child("Photo")
                .child(mphoto.getPhoto_id())
                .child("comments")
                .child(commentID)
                .setValue(comment);

        //insert into user_photos node
        myRef.child("User_Photo")
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child("comments")
                .child(commentID)
                .setValue(comment);

        addCommentNotification(comment.getComment(),mphoto.getUser_id(),mphoto.getPhoto_id());

    }
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private void getCommentList(){
        Log.d(TAG, "getCommentList: Comments");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

//        if(mphoto.getComments().size() == 0){
        if(commentCount == 0){
            mComments.clear();
            Comments firstComment = new Comments();
            firstComment.setUser_id(mphoto.getUser_id());
            firstComment.setComment(mphoto.getCaption());
            firstComment.setDate_created(mphoto.getDate_Created());
            mComments.add(firstComment);
            mphoto.setComments(mComments);
            setupWidgets();
        }


        myRef.child("Photo")
                .child(mphoto.getPhoto_id())
                .child("comments")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        Log.d(TAG, "onChildAdded: child added.");

                        Query query = myRef
                                .child("Photo")
                                .orderByChild("photo_id")
                                .equalTo(mphoto.getPhoto_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                for ( DataSnapshot singleSnapshot :  dsnapshot.getChildren()){
                                    Photo photo = new Photo();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    photo.setCaption(objectMap.get("caption").toString());
                                    photo.setTags(objectMap.get("tags").toString());
                                    photo.setPhoto_id(objectMap.get("photo_id").toString());
                                    photo.setUser_id(objectMap.get("user_id").toString());
                                    photo.setDate_Created(objectMap.get("date_Created").toString());
                                    photo.setImage_Path(objectMap.get("image_Path").toString());

                                    mComments.clear();
                                    Comments firstComment = new Comments();
                                    firstComment.setUser_id(mphoto.getUser_id());
                                    firstComment.setComment(mphoto.getCaption());
                                    firstComment.setDate_created(mphoto.getDate_Created());
                                    mComments.add(firstComment);

//                                    List<Comments> commentsList = new ArrayList<Comments>();
                                    for (DataSnapshot dSnapshot : singleSnapshot
                                            .child("comments").getChildren()){
                                        Comments comments = new Comments();
                                        comments.setUser_id(dSnapshot.getValue(Comments.class).getUser_id());
                                        comments.setComment(dSnapshot.getValue(Comments.class).getComment());
                                        comments.setDate_created(dSnapshot.getValue(Comments.class).getDate_created());
                                        mComments.add(comments);
                                    }
                                    photo.setComments(mComments);
                                    mphoto=photo;
                                    setupWidgets();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setupWidgets(){

        CommentListAdapter adapter = new CommentListAdapter(this,R.layout.layout_each_comment, mComments);
        mListView.setAdapter(adapter);

        mpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mComment.getText().toString().isEmpty()){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment(mComment.getText().toString());

                    mComment.setText("");
                    closeKeyboard();
                }else{
                    Toast.makeText(ViewComments.this, "you can't post a blank comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void addCommentNotification(String comment , String userid , String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications");

        HashMap<String, Object> hashMappp = new HashMap<>();
        hashMappp.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMappp.put("text", "Commented!"+comment);
        hashMappp.put("postid",postid);
        hashMappp.put("ispost", true);
        reference.child(userid).push().setValue(hashMappp);

    }


}