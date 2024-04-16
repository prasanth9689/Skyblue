package com.skyblue.skybluea.retrofit;

import com.skyblue.skybluea.model.ChannelCreation;
import com.skyblue.skybluea.model.ChannelUpdate;
import com.skyblue.skybluea.model.Comments;
import com.skyblue.skybluea.model.LikeVideo;
import com.skyblue.skybluea.model.Login;
import com.skyblue.skybluea.model.Post;
import com.skyblue.skybluea.model.Register;
import com.skyblue.skybluea.model.Search;
import com.skyblue.skybluea.model.UploadVideo;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {

    @FormUrlEncoded
    @POST("/skyblue/get_common_data.php")
    Call<List<Post>> getCommonPosts(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("/skyblue/get_common_data.php")
    Call<ResponseBody> getCommonPosts2(@Field("user_id") String user_id);

    @POST("/skyblue/login.php")
    Call<List<Login>> login(@Body RequestBody params);

    @Multipart
    @POST("/skyblue/post_video_upload.php")
    Call<UploadVideo> uploadVideo(@Part("video_name") RequestBody video_name,
                                  @Part("user_id") RequestBody user_id,
                                  @Part("duration") RequestBody duration,
                                  @Part("date") RequestBody date,
                                  @Part("time") RequestBody time,
                                  @Part("time_date") RequestBody time_date,
                                  @Part("description") RequestBody description,
                                  @Part MultipartBody.Part video,
                                  @Part MultipartBody.Part thumbnail,
                                  @Part("channel_id")RequestBody channel_id,
                                  @Part("channel_name")RequestBody channel_name);

    @FormUrlEncoded
    @POST("/skyblue/check_user.php")
    Call<String> check_user(@Field("mobile") String mobile);

    @POST("/skyblue/register.php")
    Call<List<Register>> register(@Body RequestBody params);

    @Multipart
    @POST("/skyblue/skyblue_main.php")
    Call<ChannelCreation> channelCreation(@Part("access") RequestBody access,
                                          @Part("channel_name") RequestBody video_name,
                                          @Part("user_id") RequestBody user_id,
                                          @Part("time_date")RequestBody time_date);

    @Multipart
    @POST("/skyblue/skyblue_main.php")
    Call<ChannelCreation> getChannels(@Part("access") RequestBody access,
                                      @Part("user_id") RequestBody user_id);

    @Multipart
    @POST("/skyblue/video_se.php")
    Call<Search> getSearch(@Part("access") RequestBody access,
                           @Part("query_base64") RequestBody query_base64,
                           @Part("user_id") RequestBody user_id,
                           @Part("time_date") RequestBody time_date);

    @Multipart
    @POST("/skyblue/update_channel_name.php")
    Call<ChannelUpdate> updateChannelName(@Part("user_id") RequestBody user_id,
                                          @Part("channel_id") RequestBody channel_id,
                                          @Part("channel_name") RequestBody channel_name);

    @Multipart
    @POST("/skyblue/post_liked.php")
    Call<LikeVideo> insertLike(@Part("user_id") RequestBody user_id,
                               @Part("post_id") RequestBody post_id);

    @Multipart
    @POST("/skyblue/post_unliked.php")
    Call<LikeVideo> insertUnLike(@Part("user_id") RequestBody user_id,
                               @Part("post_id") RequestBody post_id);

    @Multipart
    @POST("/skyblue/comment_send.php")
    Call<ResponseBody> saveComment(@Part("logged_user_id") RequestBody logged_user_id,
                                   @Part("logged_user_name") RequestBody logged_user_name,
                                   @Part("logged_user_comment") RequestBody logged_user_comment,
                                   @Part("post_id") RequestBody post_id,
                                   @Part("post_user_id") RequestBody post_user_id);

    @Multipart
    @POST("/skyblue/comments_get.php")
    Call<Comments> getComments(@Part("post_id") RequestBody post_id,
                               @Part("user_id") RequestBody user_id);

    @Multipart
    @POST("/skyblue/views_insert.php")
    Call<ResponseBody> sendViews(@Part("post_id") RequestBody post_id);

    @Multipart
    @POST("/skyblue/subscription_new.php")
    Call<ResponseBody> subscriptionNew(@Part("logged_user_id") RequestBody logged_user_id,
                                       @Part("channel_id") RequestBody channel_id);

    // ---------------------------
    @POST("/skyblue/login2.php")
    Call<List<Login>> login2(@Body RequestBody params);

    @FormUrlEncoded
    @POST("/skyblue/check_user2.php")
    Call<String> check_user_email(@Field("email_person_id") String email_person_id);

    @POST("/skyblue/register2.php")
    Call<List<Register>> registerEmailSign(@Body RequestBody params);

    @POST("/skyblue/get_email_sign_in_details.php")
    Call<List<Login>> getEmailSignInDetails(@Body RequestBody params);
}
