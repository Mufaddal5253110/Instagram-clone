package my.insta.androrealm.Messages.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import my.insta.androrealm.Messages.Adapter.FriendsAdapter;
import my.insta.androrealm.Messages.Model.Chat;
import my.insta.androrealm.Messages.Model.Chatlist;
import my.insta.androrealm.Messages.Notification.Token;
import my.insta.androrealm.R;
import my.insta.androrealm.models.Users;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private FriendsAdapter userAdapter;
    private List<Users> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.ChatsFragment_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                usersList.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Chat chat=snapshot.getValue(Chat.class);

                    assert chat != null;
                    if (chat.getSender().equals(fuser.getUid())) {

                        usersList.add(chat.getReceiver());

                    }

                    if (chat.getReceiver().equals(fuser.getUid())) {

                        usersList.add(chat.getSender());

                    }

                }


                Set<String> hashSet = new HashSet<String>(usersList);
                usersList.clear();
                usersList.addAll(hashSet);


                readChats();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UpdateToken();
        return view;
    }

    private void UpdateToken() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    private void readChats() {

        mUsers=new ArrayList<>();

        reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //mUsers.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Users user=snapshot.getValue(Users.class);

                    for(String id:usersList){

                        assert user != null;
                        if (user.getUser_id().equals(id)) {

                            mUsers.add(user);

                        }

                    }

                }

                userAdapter=new FriendsAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}