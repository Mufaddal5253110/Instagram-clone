package my.insta.androrealm.Messages.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import my.insta.androrealm.Messages.Adapter.FriendsAdapter;
import my.insta.androrealm.R;
import my.insta.androrealm.Utils.SearchUsersAdapter;
import my.insta.androrealm.models.Users;


public class FriendsFragment extends Fragment {

    private static final String TAG ="FriendsFragment" ;

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private List<Users> mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = view.findViewById(R.id.FragmentFriends_userList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUser = new ArrayList<>();

        readUsers();

        return view;
    }

    private void readUsers() {

        // Retriving all users except self

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Users users = snapshot1.getValue(Users.class);
                    if(!users.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Log.d(TAG, "onDataChange: userid:"+FirebaseAuth.getInstance().getCurrentUser().getUid());
                        mUser.add(users);
                    }
                }
                updateFriendList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateFriendList() {

        Log.d(TAG,"updateFriendList : Updating Friend List");

        friendsAdapter = new FriendsAdapter(getContext(),mUser,false);
        recyclerView.setAdapter(friendsAdapter);

    }
}