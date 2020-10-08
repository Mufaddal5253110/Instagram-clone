package my.insta.androrealm.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import my.insta.androrealm.R;
import my.insta.androrealm.Utils.SearchUsersAdapter;
import my.insta.androrealm.models.Users;

public class FollowersFollowing extends AppCompatActivity {

    String id,title,number;

    List<String> idList;
    RecyclerView recyclerView;
    TextView Title,Number;
    SearchUsersAdapter usersAdapter;
    List<Users> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_following);

        Title = (TextView) findViewById(R.id.FollowFollowing_txt);
        Number = (TextView) findViewById(R.id.FollowFollowing_number);
        recyclerView = (RecyclerView) findViewById(R.id.FollowFollowing_recyclerView);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        number = intent.getStringExtra("number");

        Title.setText(title);
        Number.setText(number);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersList = new ArrayList<>();
        usersAdapter = new SearchUsersAdapter(this,usersList);
        recyclerView.setAdapter(usersAdapter);

        idList = new ArrayList<>();

        switch (title){

            case ("Followers"):
                getFollowers();
                break;
            case ("Following"):
                getFollowings();
                break;
            case "Views":
                getViews();
                break;

        }

    }

    private void getFollowings() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Following").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Followers").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    idList.add(dataSnapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Users users = dataSnapshot.getValue(Users.class);
                    for(String id : idList){
                        if(users.getUser_id().equals(id)){
                            usersList.add(users);
                        }
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getViews(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(id).child(getIntent().getStringExtra("storyid")).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}