package ru.netology.nmedia.api

import retrofit2.Call
import retrofit2.http.GET
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*



private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

interface PostApiService{

    @GET("posts")
    fun getAll(): Call<List<Post>>

    @DELETE("posts/{id}")
    fun removeById(@Path("id")id:Long):Call<Unit>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id")id:Long):Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikeById(@Path("id") id: Long): Call<Post>

        @POST("posts")
    fun save(@Body post: Post): Call<Unit>

}

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

object PostsApi {
    val retrofitService: PostApiService by lazy {
        retrofit.create()
    }
}