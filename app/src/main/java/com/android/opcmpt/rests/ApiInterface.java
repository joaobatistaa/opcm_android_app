package com.android.opcmpt.rests;

import com.android.opcmpt.Config;
import com.android.opcmpt.callbacks.CallbackComments;
import com.android.opcmpt.callbacks.CallbackEvents;
import com.android.opcmpt.callbacks.CallbackEventsDetails;
import com.android.opcmpt.callbacks.CallbackNewsDetail;
import com.android.opcmpt.callbacks.CallbackRecent;
import com.android.opcmpt.models.PushNotif;
import com.android.opcmpt.models.Settings;
import com.android.opcmpt.models.Value;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "Data-Agent: OPCM News App";

    @Headers({CACHE, AGENT})
    @GET("api/get_news_detail/?id=")
    Call<PushNotif> getNotificationDetail(
            @Query("id") long id
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_events_detail/?id=")
    Call<PushNotif> getNotificationDetail2(
            @Query("id") long id
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_postevent_detail")
    Call<CallbackEventsDetails> getEventsDetail(
            @Query("id") long id
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_post_detail")
    Call<CallbackNewsDetail> getPostDetail(
            @Query("id") long id
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_recent_posts/?api_key=" + Config.API_KEY )
    Call<CallbackRecent> getRecentPost(
            @Query("page") int page,
            @Query("count") int count
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_recent_events/?api_key=" + Config.API_KEY )
    Call<CallbackEvents> getEventsPost(
            @Query("page") int page,
            @Query("count") int count
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_video_posts/?api_key=" + Config.API_KEY )
    Call<CallbackRecent> getVideoPost(
            @Query("page") int page,
            @Query("count") int count
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_category_index/?api_key=" + Config.API_KEY)
    Call<CallbackEvents> getAllCategories();

    @Headers({CACHE, AGENT})
    @GET("api/get_category_posts" )
    Call<CallbackEventsDetails> getCategoryDetailsByPage(
            @Query("id") long id,
            @Query("page") long page,
            @Query("count") long count
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_search_results/?api_key=" + Config.API_KEY)
    Call<CallbackRecent> getSearchPosts(
            @Query("search") String search,
            @Query("count") int count
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_comments")
    Call<CallbackComments> getComments(@Query("nid") Long nid
    );

    @FormUrlEncoded
    @POST("api/post_comment")
    Call<Value> sendComment(@Field("nid") Long nid,
                            @Field("user_id") String user_id,
                            @Field("content") String content,
                            @Field("date_time") String date_time);

    @FormUrlEncoded
    @POST("api/update_comment")
    Call<Value> updateComment(@Field("comment_id") String comment_id,
                              @Field("date_time") String date_time,
                              @Field("content") String content);

    @FormUrlEncoded
    @POST("api/delete_comment")
    Call<Value> deleteComment(@Field("comment_id") String comment_id);

    @Headers({CACHE, AGENT})
    @GET("api/get_privacy_policy")
    Call<Settings> getPrivacyPolicy();


    @Headers({CACHE, AGENT})
    @GET("api/get_about_us")
    Call<Settings> getAboutUs();

    @Headers({CACHE, AGENT})
    @GET("api/get_our_mission")
    Call<Settings> getOurMission();

}
