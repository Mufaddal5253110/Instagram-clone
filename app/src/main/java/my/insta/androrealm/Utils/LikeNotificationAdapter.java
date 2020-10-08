package my.insta.androrealm.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import my.insta.androrealm.R;
import my.insta.androrealm.Search.UserSearchProfileActivity;
import my.insta.androrealm.Search.UserSearchViewPost;
import my.insta.androrealm.models.Notification;
import my.insta.androrealm.models.Photo;
import my.insta.androrealm.models.Users;

public class LikeNotificationAdapter extends RecyclerView.Adapter<LikeNotificationAdapter.ViewHolder>{

    String TAG = "LikeNotificationAdapter";

    private Context mcontext;
    private List<Notification> mNotification;

    public LikeNotificationAdapter(Context mcontext, List<Notification> mNotification) {
        this.mcontext = mcontext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.like_each_item_layout,parent,false);
        return new LikeNotificationAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notification notification = mNotification.get(position);

        holder.text.setText(notification.getText());
        getUserInfo(holder.profileImage,holder.usernamee,notification.getUserid());
        
        if(notification.isIspost()){
            Log.d(TAG, "onBindViewHolder: Notification for Post");
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage,notification.getPostid());
        }else{
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(notification.isIspost()){

                    Log.d(TAG, "onClick: Notification Item Clicked: Redirecting to ViewPost page");
//                Intent intent=new Intent(UserSearchProfileActivity.this, UserSearchViewPost.class);
//                intent.putExtra("SearchedUserPhoto",photos.get(position));
//                intent.putExtra("Commentcount",photos.get(position).getComments().size());
//                mcontext.startActivity(intent);

                }else {
                    Log.d(TAG, "onClick: Notification Item Clicked: Redirecting to Profile page");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImage;
        public ImageView postImage;
        public TextView usernamee,text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.like_each_item_user_img);
            postImage = itemView.findViewById(R.id.like_each_item_post_image);
            usernamee = itemView.findViewById(R.id.like_each_item_username);
            text = itemView.findViewById(R.id.like_each_item_comment);

        }
    }
    private void getUserInfo(final ImageView profimage, final TextView username, String publisherId){
        Log.d(TAG, "getUserInfo: Getting Profileimage,Username : UserId:"+publisherId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Glide.with(mcontext).load(users.getProfilePhoto()).into(profimage);
                username.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getPostImage(final ImageView postimg, String postid){
        Log.d(TAG, "getPostImage: Getting PostImage with postId :"+postid);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Photo")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
//                Photo photo = dsnapshot.getValue(Photo.class);
                Log.d(TAG, "onDataChange: getPostImage:ImagePath:"+dsnapshot.child("image_Path").getValue());
                final ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(dsnapshot.child("image_Path").getValue().toString(), postimg);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
