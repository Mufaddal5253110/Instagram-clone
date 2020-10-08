package my.insta.androrealm.Messages.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import my.insta.androrealm.Messages.MessageActivity;
import my.insta.androrealm.Messages.Model.Chat;
import my.insta.androrealm.R;
import my.insta.androrealm.Utils.SearchUsersAdapter;
import my.insta.androrealm.models.Users;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    String TAG = "FriendsAdapter";

    private Context mcontext;
    private List<Users> muser;
    private boolean ischat;

    private FirebaseUser firebaseUser;
    String theLastMessage;
    Users users;

    public FriendsAdapter(Context mcontext, List<Users> muser, boolean ischat) {
        this.mcontext = mcontext;
        this.muser = muser;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.friends_single_layout,parent,false);
        return new FriendsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        users = muser.get(position);
        holder.username.setText(users.getUsername());
//        holder.fullname.setText(users.getFullName());
        Glide.with(mcontext)
                .load(users.getProfilePhoto())
                .into(holder.profileimage);

        if (ischat){
            lastMessage(users.getUser_id(), holder.last_msg);
        } else {
            holder.last_msg.setText(users.getFullName());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, MessageActivity.class);
                intent.putExtra("userid",users.getUser_id());
                mcontext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return muser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username,last_msg;
        public CircleImageView profileimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = (TextView)itemView.findViewById(R.id.FriendSingle_userName);
            last_msg = (TextView)itemView.findViewById(R.id.FriendSingle_lastMsg);
            profileimage = (CircleImageView)itemView.findViewById(R.id.FriendSingle_user_img);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText(users.getFullName());
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
