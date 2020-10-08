package my.insta.androrealm.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import my.insta.androrealm.Profile.ViewPostFragment;
import my.insta.androrealm.R;
import my.insta.androrealm.models.Comments;
import my.insta.androrealm.models.Photo;
import my.insta.androrealm.models.Users;

public class CommentListAdapter extends ArrayAdapter<Comments> {

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, @LayoutRes int resource,
                              @NonNull List<Comments> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder{
        TextView comment, timestamp, reply, likes;
        CircleImageView profileImage;
        ImageView like;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.comment = (TextView) convertView.findViewById(R.id.comment_posted);
            holder.timestamp = (TextView) convertView.findViewById(R.id.comment_time_posted);
            holder.reply = (TextView) convertView.findViewById(R.id.comment_reply);
            holder.likes = (TextView) convertView.findViewById(R.id.comment_likes);
            holder.like = (ImageView) convertView.findViewById(R.id.comment_like_btn);
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.user_img);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        //set the timestamp difference
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timestamp.setText(timestampDifference + " d");
        }else{
            holder.timestamp.setText("today");
        }

        //set the username and profile photo
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("Users")
                .orderByChild("user_id")
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                    //set the comment
                    holder.comment.setText(singleSnapshot.getValue(Users.class)
                            .getUsername()+" "+getItem(position).getComment());

                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(
                            singleSnapshot.getValue(Users.class).getProfilePhoto(),
                            holder.profileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        try{
            if(position == 0){
                holder.like.setVisibility(View.GONE);
                holder.likes.setVisibility(View.GONE);
                holder.reply.setVisibility(View.GONE);
            }else {
                holder.likes.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){
            Toast.makeText(mContext, "getView: NullPointerException:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }



        return convertView;
    }

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(Comments comment){

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = comment.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Toast.makeText(getContext(),"getTimestampDifference: ParseException: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            difference = "0";
        }
        return difference;
    }

}
