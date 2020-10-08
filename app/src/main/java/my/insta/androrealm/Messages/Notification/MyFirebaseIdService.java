package my.insta.androrealm.Messages.Notification;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser !=null)
        {
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken)
    {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        Token token=new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

}
