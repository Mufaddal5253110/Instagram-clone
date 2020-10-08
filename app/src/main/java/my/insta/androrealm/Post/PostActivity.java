package my.insta.androrealm.Post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import my.insta.androrealm.Home;
import my.insta.androrealm.R;
import my.insta.androrealm.Utils.methods;
import my.insta.androrealm.models.Photo;
import my.insta.androrealm.models.Users;

public class PostActivity extends AppCompatActivity {

    ImageView postNow,backFromPost,addedImage;
    EditText addedCaption,AddedTag;

    DatabaseReference databaseReference,data;
    StorageReference storageReference,ref;

    methods method;

    int count = 0;
    int PICK_IMAGE_REQUEST=1;

    Uri imageUri;
    String RandomUId,userId;
    String postCount;
    String caption,tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postNow = (ImageView)findViewById(R.id.post_now);
        backFromPost = (ImageView)findViewById(R.id.back_from_post);
        addedImage = (ImageView)findViewById(R.id.added_image);
        addedCaption = (EditText)findViewById(R.id.added_caption);
        AddedTag = (EditText)findViewById(R.id.added_tags);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        method = new methods();
        count = getCount();

        backFromPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, Home.class));
                finish();
            }
        });

        postNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });

        openFileChooser();



    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            addedImage.setImageURI(imageUri);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadimage() {
        if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(PostActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            caption = addedCaption.getText().toString().trim();
            tags = AddedTag.getText().toString().trim();

            RandomUId = UUID.randomUUID().toString();
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ref = storageReference.child("photos/users/"+"/"+userId+"/photo"+(count+1));
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            increasePostCount(count);
                            addPost(caption, getTimestamp(), String.valueOf(uri), RandomUId, userId, tags);
                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this, "Posted successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PostActivity.this,Home.class));
                            finish();


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostActivity.this,Home.class));
                    finish();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            });

        }

    }

//******************************FUNCTION TO ADD PHOTO TO FIREBASE STORAGE********
    public void addPost(String caption, String date_Created, String image_Path, String photo_id, String user_id, String tags){

        HashMap<String, String> hashMappp = new HashMap<>();
        hashMappp.put("caption", caption);
        hashMappp.put("date_Created", date_Created);
        hashMappp.put("image_Path", image_Path);
        hashMappp.put("photo_id", photo_id);
        hashMappp.put("tags", tags);
        hashMappp.put("user_id", user_id);
        databaseReference.child("User_Photo").child(user_id).child(photo_id).setValue(hashMappp);
        databaseReference.child("Photo").child(photo_id).setValue(hashMappp);


    }

//******************************FUNCTION TO INCREASE POST COUNT********
    public void increasePostCount(final int count){


        data = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postCount = Integer.toString(count+1);
                data.child("posts").setValue(postCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

//******************************FUNCTION TO GET POST TIME********
    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
//******************************FUNCTION TO GET POST Count********
    public int getCount() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = method.getImagecount(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return count;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}