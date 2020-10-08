package my.insta.androrealm.Messages.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAAtQdAcU:APA91bEQlb_cftwY8wvTcsDAQgoYk86w1PZP2bAwuOtH0asg_S4ZMDmQu85NsgvBgP7PFIQMNsckt3USVW97U7r-anwa2vNrcrmvw_dgRdO-_nE4I5S_uaZMMkfdBbO6lD7zn_EfZDk-"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);


}
