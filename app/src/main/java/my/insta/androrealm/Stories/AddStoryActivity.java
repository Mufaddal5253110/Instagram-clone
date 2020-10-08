package my.insta.androrealm.Stories;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import my.insta.androrealm.MainActivity;
import my.insta.androrealm.R;

public class AddStoryActivity extends AppCompatActivity {

    private static final String TAG = "AddStoryActivity";
    private Uri mImageUri;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        storageRef = FirebaseStorage.getInstance().getReference("story");

        CropImage.activity()
                .setAspectRatio(9,16)
                .start(AddStoryActivity.this);
    }

    private String getFileExtension(Uri uri){
        Log.d(TAG, "getFileExtension: "+uri);
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage_10(){
        Log.d(TAG, "uploadImage_10: Uploading Story");
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting...");
        pd.show();
        if (mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        miUrlOk = downloadUri.toString();

                        String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                                .child(myid);

                        String storyid = reference.push().getKey();
                        long timeend = System.currentTimeMillis()+86400000; // 1 day later

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", miUrlOk);
                        hashMap.put("timestart", ServerValue.TIMESTAMP);
                        hashMap.put("timeend", timeend);
                        hashMap.put("storyid", storyid);
                        hashMap.put("userid", myid);

                        reference.child(storyid).setValue(hashMap);

                        pd.dismiss();

                        finish();

                    } else {
                        Toast.makeText(AddStoryActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+e.getMessage());
                    Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(AddStoryActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage_10();

        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddStoryActivity.this, MainActivity.class));
            finish();
        }
    }
}