package my.insta.androrealm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import my.insta.androrealm.ReusableCode.ReusableCodeForAll;
import my.insta.androrealm.models.Passwords;
import my.insta.androrealm.models.Users;
import my.insta.androrealm.models.privatedetails;

public class Registration extends AppCompatActivity {

    TextView alreadyhaveacc;
    TextInputLayout Fname,Username, Email, Pass, Mobileno,Gender,Description,Website;
    EditText Birth;
    int year,month,day;
    Button register;
    CountryCodePicker Cpp;
    FirebaseAuth FAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String fname,username,email,pass,mobileno,gender,description,website,birth;
    String useridd;
    AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        alreadyhaveacc = (TextView)findViewById(R.id.AlreadyHavesignin);
        Birth = (EditText)findViewById(R.id.birthdate);
        Fname = (TextInputLayout) findViewById(R.id.Fullname);
        Username = (TextInputLayout) findViewById(R.id.Username);
        Email = (TextInputLayout) findViewById(R.id.signup_email);
        Pass = (TextInputLayout) findViewById(R.id.signup_password);
        Gender = (TextInputLayout) findViewById(R.id.gender);
        Mobileno = (TextInputLayout) findViewById(R.id.mobilenoo);
        Description = (TextInputLayout) findViewById(R.id.bio);
        Website = (TextInputLayout) findViewById(R.id.website);

        Cpp = (CountryCodePicker) findViewById(R.id.countrycode);

        register = (Button) findViewById(R.id.signup_button);

//******************************BACKGROUND ANIMATION*************************
        RelativeLayout container = (RelativeLayout) findViewById(R.id.relative_registration);

        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);

//******************************BACKGROUND ANIMATION*************************



        Birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Registration.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Birth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },year,month,day);
                datePickerDialog.show();

            }
        });

        alreadyhaveacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
                finish();
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        FAuth = FirebaseAuth.getInstance();
//        useridd = FAuth.getCurrentUser().getUid();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fname = Fname.getEditText().getText().toString().trim();
                username = Username.getEditText().getText().toString().trim();
                email = Email.getEditText().getText().toString().trim();
                mobileno = Mobileno.getEditText().getText().toString().trim();
                pass = Pass.getEditText().getText().toString().trim();
                gender = Gender.getEditText().getText().toString().trim();
                description = Description.getEditText().getText().toString().trim();
                birth = Birth.getText().toString().trim();
                website = Website.getEditText().getText().toString().trim();

                if (isValid()) {

                    databaseReference.child("Users").orderByChild("Username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(Registration.this, "Username already exists. Please try other username.", Toast.LENGTH_SHORT).show();


                            }else {
                                final ProgressDialog mDialog = new ProgressDialog(Registration.this);
                                mDialog.setCancelable(false);
                                mDialog.setCanceledOnTouchOutside(false);
                                mDialog.setMessage("Registering please wait...");
                                mDialog.show();

                                FAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            useridd = FAuth.getCurrentUser().getUid();

                                            addUsers(description,fname,username,website);
                                            addPrivateDetails(useridd,email,gender,birth,mobileno);
                                            addPasswords(pass);

                                            mDialog.dismiss();

                                            FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
                                                        builder.setMessage("Registered Successfully,Please Verify your Email");
                                                        builder.setCancelable(false);
                                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                dialog.dismiss();

                                                                String phonenumber = Cpp.getSelectedCountryCodeWithPlus() + mobileno;
                                                                Intent b = new Intent(Registration.this, VerifyPhone.class);
                                                                b.putExtra("phonenumber", phonenumber);
                                                                startActivity(b);

                                                            }
                                                        });
                                                        AlertDialog alert = builder.create();
                                                        alert.show();

                                                    } else {
                                                        mDialog.dismiss();
                                                        ReusableCodeForAll.ShowAlert(Registration.this, "Error", task.getException().getMessage());

                                                    }
                                                }
                                            });


                                        } else {
                                            mDialog.dismiss();
                                            ReusableCodeForAll.ShowAlert(Registration.this, "Error", task.getException().getMessage());
                                        }

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }



            }
        });

    }
    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid() {
        Email.setErrorEnabled(false);
        Email.setError("");
        Fname.setErrorEnabled(false);
        Fname.setError("");
        Username.setErrorEnabled(false);
        Username.setError("");
        Pass.setErrorEnabled(false);
        Pass.setError("");
        Mobileno.setErrorEnabled(false);
        Mobileno.setError("");
        Gender.setErrorEnabled(false);
        Gender.setError("");
//        Birth.setErrorEnabled(false);
//        Birth.setError("");

        boolean isValidname = false, isValidemail = false, isvalidpassword = false, isvalid = false, isvalidmobileno = false, isvalidgender = false , isvalidusername=false;
        if (TextUtils.isEmpty(fname)) {
            Fname.setErrorEnabled(true);
            Fname.setError("Fullname is required");
        } else {
            isValidname = true;
        }
        if (TextUtils.isEmpty(email)) {
            Email.setErrorEnabled(true);
            Email.setError("Email is required");
        } else {
            if (email.matches(emailpattern)) {
                isValidemail = true;
            } else {
                Email.setErrorEnabled(true);
                Email.setError("Enter a valid Email Address");
            }

        }
        if (TextUtils.isEmpty(pass)) {
            Pass.setErrorEnabled(true);
            Pass.setError("Password is required");
        } else {
            if (pass.length() < 6) {
                Pass.setErrorEnabled(true);
                Pass.setError("password is too weak");
            } else {
                isvalidpassword = true;
            }
        }
        if (TextUtils.isEmpty(mobileno)) {
            Mobileno.setErrorEnabled(true);
            Mobileno.setError("Mobile number is required");
        } else {
            if (mobileno.length() < 10) {
                Mobileno.setErrorEnabled(true);
                Mobileno.setError("Invalid mobile number");
            } else {
                isvalidmobileno = true;
            }
        }
        if (TextUtils.isEmpty(gender)) {
            Gender.setErrorEnabled(true);
            Gender.setError("Field cannot be empty");
        } else {
            isvalidgender = true;
        }if (TextUtils.isEmpty(username)) {
            Username.setErrorEnabled(true);
            Username.setError("Field cannot be empty");
        } else {
            isvalidusername = true;
        }

        isvalid = (isValidname  && isValidemail && isvalidpassword && isvalidmobileno &&  isvalidgender && isvalidusername) ? true : false;
        return isvalid;
    }

//******************************FUNCTIONS TO ADD DATA'S TO FIREBASE*************************
    public void addUsers(String Discription,String FullName,String Username,String Website){

        Users user = new Users(
                Discription,
                "0",
                "0",
                FullName,
                "0",
                "https://firebasestorage.googleapis.com/v0/b/instagram-clone-291e7.appspot.com/o/generalProfilePhoto%2Fdefualt_insta_pic.png?alt=media&token=e9834979-a141-48fd-87b6-a2074e7dbc9b",
                Username,
                Website,
                useridd
        );
        databaseReference.child("Users").child(useridd).setValue(user);
    }
    public void addPrivateDetails(String user_id, String email, String gender, String birthdate, String phoneNumber){

        privatedetails details = new privatedetails(
                user_id,
                email,
                gender,
                birthdate,
                phoneNumber
        );
        databaseReference.child("Privatedetails").child(useridd).setValue(details);
    }
    public void addPasswords(String passwords){

        Passwords pass = new Passwords(passwords);
        databaseReference.child("Passwords").child(useridd).setValue(pass);

    }
//*******************************************************************************

//******************************BACKGROUND ANIMATION*************************
    // Starting animation:- start the animation on onResume.
    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    // Stopping animation:- stop the animation on onPause.
    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }
//****************************************************************************

}