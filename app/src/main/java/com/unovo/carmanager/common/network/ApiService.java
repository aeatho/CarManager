package com.unovo.carmanager.common.network;

import com.unovo.carmanager.bean.FriendInfo;
import com.unovo.carmanager.bean.MessageInfo;
import com.unovo.carmanager.bean.MovementInfo;
import com.unovo.carmanager.bean.UserInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/7/20.
 */
public interface ApiService {
  @GET("DesktopModules/Modules.Chefutong/Api/Users/Login") Call<UserInfo> Login(
      @Query("name") String name, @Query("pwd") String pwd);

  @GET("DesktopModules/Modules.Chefutong/Api/Users/GetFriends") Call<List<FriendInfo>> getfriends(
      @Query("userid") String uid, @Query("pageindex") int pageindex,
      @Query("pagesize") int pagesize);

  /**
   * sos
   */
  @GET("DesktopModules/Modules.Chefutong/Api/UploadData/RescueAdd") Call<String> sos(
      @Query("userid") String uid, @Query("lat") double lat, @Query("lon") double lon,
      @Query("time") String time, @Query("speed") double speed,@Query("type") int type);

  /**
   * 实时上传位置信息
   */
  @GET("DesktopModules/Modules.Chefutong/Api/UploadData/UploadGPS") Call<String> uploadGPS(
      @Query("userid") String uid, @Query("lat") double lat, @Query("lon") double lon,
      @Query("time") String time, @Query("speed") double speed);

  @GET("DesktopModules/Modules.Chefutong/Api/Activity/GetActivities")
  Call<List<MovementInfo>> movement(@Query("activityID") int aid, @Query("title") String title,
      @Query("description") String desc, @Query("activityTime") String MovementTime,
      @Query("activityPlace") String place, @Query("auditStatus") boolean status,
      @Query("createdBy") String bywho, @Query("createdOn") String createTime);

  @GET("DesktopModules/Modules.Chefutong/API/VoiceMsg/GetVoiceMsgs")
  Call<List<MessageInfo>> message(@Query("userid") String uid, @Query("pageindex") int pageindex,
      @Query("pagesize") int pagesize, @Query("time") String time, @Query("path") String path);

  @GET("DesktopModules/Modules.Chefutong/Api/UploadData/UserAndTruckStatus") Call<String> status(
      @Query("userid") String uid, @Query("lat") double lat, @Query("lon") double lon,
      @Query("isintruck") boolean status);

  @GET("DesktopModules/Modules.Chefutong/Api/Users/logoff") Call<String> logout(
      @Query("name") String uname);
}
