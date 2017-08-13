package hu.bitnet.civilparking.RequestInterfaces;

import hu.bitnet.civilparking.ServerResponses.ServerResponseError;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Attila on 2017.08.13..
 */

public interface RequestInterfaceLoginError {

    @POST("login")
    @FormUrlEncoded
    Call<ServerResponseError> post(@Field(encoded = true, value = "email") String email, @Field("password") String password);

}
