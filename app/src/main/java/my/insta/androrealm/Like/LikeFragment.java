package my.insta.androrealm.Like;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import my.insta.androrealm.R;
import my.insta.androrealm.Utils.LikeNotificationAdapter;
import my.insta.androrealm.Utils.SearchUsersAdapter;
import my.insta.androrealm.models.Notification;
import my.insta.androrealm.models.Users;

public class LikeFragment extends Fragment {

    private static final String TAG ="LikeFragment" ;
    private RecyclerView recyclerView;
    private LikeNotificationAdapter likeNotificationAdapter;
    private List<Notification> notificationList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_like,null);

        recyclerView = (RecyclerView)v.findViewById(R.id.FragmentLike_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationList = new ArrayList<>();
        likeNotificationAdapter = new LikeNotificationAdapter(getContext(),notificationList);
        recyclerView.setAdapter(likeNotificationAdapter);
        
        readNotifications();

        return v;
    }

    private void readNotifications() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    notificationList.add(notification);
                }
                Collections.reverse(notificationList);
                likeNotificationAdapter.notifyDataSetChanged();
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

}
