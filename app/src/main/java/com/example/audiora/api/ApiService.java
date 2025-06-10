package com.example.audiora.api;

import retrofit2.Call;
//import retrofit2.Response;
import com.example.audiora.model.Response;
import com.example.audiora.model.rss.RssResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("search")
    Call<Response> searchTrack(
            @Query("term") String searchTerm,
            @Query("media") String mediaType,
            @Query("entity") String entity,
            @Query("limit") int limit);

    @GET("search?media=music&entity=song")
    Call<Response> searchMusicSongs(
            @Query("term") String searchTerm,
            @Query("limit") int limit);

    @GET("lookup")
    Call<Response> lookupTrackById(@Query("id") String trackId);

    @GET("{country}/rss/topsongs/limit={limit}/json")
    Call<RssResponse> getTopSongs(
            @Path("country") String country,
            @Path("limit") int limit);
}
