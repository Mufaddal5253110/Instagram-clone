package my.insta.androrealm.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import my.insta.androrealm.R;
import my.insta.androrealm.Search.UserSearchProfileActivity;
import my.insta.androrealm.models.Users;

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.ViewHolder> {

    private Context mcontext;
    private List<Users> muser;
    String TAG = "SearchUserAdapter";
//    private LayoutInflater inflater;


    private FirebaseUser firebaseUser;

    public SearchUsersAdapter(Context mcontext, List<Users> muser) {
        this.mcontext = mcontext;
        this.muser = muser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_search_items,parent,false);
        return new SearchUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Users users = muser.get(position);
        holder.username.setText(users.getUsername());
        holder.fullname.setText(users.getFullName());
        Glide.with(mcontext)
                .load(users.getProfilePhoto())
                .into(holder.profileimage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Users Profile UID "+users.getUser_id());
                Intent intent=new Intent(mcontext, UserSearchProfileActivity.class);
                intent.putExtra("SearchedUserid",users.getUser_id());
                mcontext.startActivity(intent);

//                if(users.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                    Log.d(TAG, "Same Users Profile UID "+users.getUser_id());
//
//                }else{
//                    Log.d(TAG, "Other Users Profile UID "+users.getUser_id());
//                    Intent intent=new Intent(mcontext, UserSearchProfileActivity.class);
//                    intent.putExtra("SearchedUserid",users.getUser_id());
//                    mcontext.startActivity(intent);
//                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return muser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username,fullname;
        public CircleImageView profileimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = (TextView)itemView.findViewById(R.id.userName);
            fullname = (TextView)itemView.findViewById(R.id.fullName);
            profileimage = (CircleImageView)itemView.findViewById(R.id.user_img);
        }
    }
}
