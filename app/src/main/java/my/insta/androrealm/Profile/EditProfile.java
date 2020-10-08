package my.insta.androrealm.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import my.insta.androrealm.R;
import my.insta.androrealm.models.Users;
import my.insta.androrealm.models.privatedetails;

public class EditProfile extends AppCompatActivity {

    ImageView mProfilePhoto;
    TextInputEditText name,username,bio,website;
    String Name,Username,Bio,Website,profile;
    DatabaseReference databaseReference,data;
    StorageReference storageReference,reff;
    TextView Email,Phonenumber,Gender,Birth;
    ImageView submit;
    String useridd;
    int PICK_IMAGE_REQUEST=1;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mProfilePhoto = (ImageView)findViewById(R.id.user_img);
        name = (TextInputEditText) findViewById(R.id.Namee);
        username = (TextInputEditText)findViewById(R.id.Usernamee);
        bio = (TextInputEditText)findViewById(R.id.Bioo);
        website = (TextInputEditText)findViewById(R.id.Websitee);
        submit = (ImageView)findViewById(R.id.rightt);
        Email = (TextView)findViewById(R.id.email);
        Phonenumber = (TextView)findViewById(R.id.phonenumber);
        Gender = (TextView)findViewById(R.id.gender);
        Birth = (TextView)findViewById(R.id.birth);


        storageReference = FirebaseStorage.getInstance().getReference();


//******************************RETRIEVING DATA***************************
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Users user = snapshot.getValue(Users.class);
                name.setText(user.getFullName());
                username.setText(user.getUsername());
                bio.setText(user.getDiscription());
                website.setText(user.getWebsite());
                Glide.with(EditProfile.this)
                        .load(user.getProfilePhoto())
                        .into(mProfilePhoto);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference("Privatedetails")
                .child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final privatedetails privatedetail = snapshot.getValue(privatedetails.class);
                Email.setText(privatedetail.getEmail());
                Phonenumber.setText(privatedetail.getPhoneNumber());
                Gender.setText(privatedetail.getGender());
                Birth.setText(privatedetail.getBirthdate());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//************************************************************************


        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Name = name.getText().toString().trim();
                Username = username.getText().toString().trim();
                Bio = bio.getText().toString().trim();
                Website = website.getText().toString().trim();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("Users").orderByChild("Username").equalTo(Username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Toast.makeText(EditProfile.this, "Username already exists. Please try other username.", Toast.LENGTH_SHORT).show();
                        }else{
                            useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            data = FirebaseDatabase.getInstance().getReference("Users").child(useridd);
                            data.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) { final ProgressDialog mDialog = new ProgressDialog(EditProfile.this);
                                    mDialog.setCancelable(false);
                                    mDialog.setCanceledOnTouchOutside(false);
                                    mDialog.setMessage("Updating please wait...");
                                    mDialog.show();
                                    data.child("fullName").setValue(Name);
                                    data.child("username").setValue(Username);
                                    data.child("discription").setValue(Bio);
                                    data.child("website").setValue(Website);
                                    // Set profile photo
                                    if (imageUri != null) {

                                        reff = storageReference.child("photos/users/"+"/"+useridd+"/profilephoto");
                                        reff.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                reff.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        data.child("profilePhoto").setValue(uri.toString());


                                                    }
                                                });

                                            }
                                        });

                                    }


                                    mDialog.dismiss();
                                    Toast.makeText(EditProfile.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EditProfile.this,Account_Settings.class));
                                    finish();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

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
            mProfilePhoto.setImageURI(imageUri);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }



//        https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.vexels.com%2Fpng-svg%2Fpreview%2F147102%2Finstagram-profile-icon&psig=AOvVaw0Liq2WBgqkhzMz_UQkcP5T&ust=1600009441788000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCIiNu-nx4-sCFQAAAAAdAAAAABAD

}