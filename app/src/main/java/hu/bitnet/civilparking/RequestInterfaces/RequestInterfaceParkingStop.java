package hu.bitnet.civilparking.RequestInterfaces;

import hu.bitnet.civilparking.ServerResponses.ServerResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Attila on 2017.08.13..
 */

public interface RequestInterfaceParkingStop {

    @POST("parking_stop")
    @FormUrlEncoded
    Call<ServerResponse> post(@Field("sessionId") String sessionId, @Field("id") String id);

}
