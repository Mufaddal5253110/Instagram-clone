package my.insta.androrealm.Search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import my.insta.androrealm.Profile.ProfileFragment;
import my.insta.androrealm.Profile.ViewPostFragment;
import my.insta.androrealm.Utils.GridImageAdapter;
import my.insta.androrealm.Utils.SearchUsersAdapter;
import my.insta.androrealm.R;
import my.insta.androrealm.models.Comments;
import my.insta.androrealm.models.Likes;
import my.insta.androrealm.models.Photo;
import my.insta.androrealm.models.Users;

public class SearchFragment extends Fragment {

    private static final String TAG ="SearchFragment" ;
    private RecyclerView recyclerView;
    private SearchUsersAdapter searchUsersAdapter;
    private List<Users> mUser;

    EditText search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search,null);

        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search = (EditText)v.findViewById(R.id.search_user);

        mUser = new ArrayList<>();
        readUsers();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchUsers(s.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

    private void searchUsers(String s){

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    mUser.add(users);
                }
                updateSearchList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readUsers(){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(search.getText().toString().equals("")){
                    mUser.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Users users = snapshot1.getValue(Users.class);
                        mUser.add(users);
                    }
                    updateSearchList();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

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

    private void updateSearchList(){

        Log.d(TAG,"updateSearchList : Updating Search List");

        searchUsersAdapter = new SearchUsersAdapter(getContext(),mUser);
        recyclerView.setAdapter(searchUsersAdapter);


    }

}
