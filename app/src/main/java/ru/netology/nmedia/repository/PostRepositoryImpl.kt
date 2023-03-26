package ru.netology.nmedia.repository

import okhttp3.internal.platform.android.AndroidLogHandler.close
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl : PostRepository {

    override fun getAll(callback: PostRepository.GetAllCallBack<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })

    }


    override fun likeById(post: Post, callback: PostRepository.GetAllCallBack<Post>) {

        if (!post.likedByMe) {
            PostsApi.retrofitService.likeById(post.id).enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            }
            )
        } else {
            PostsApi.retrofitService.dislikeById(post.id).enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            }
            )
        }
    }

    override fun save(post: Post, callback: PostRepository.GetAllCallBack<Unit>) {
        PostsApi.retrofitService.save(post).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(close())

            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }
        /*val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            /* .execute()
             .close()*/
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess(close())
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }*/

    override fun removeById(id: Long, callback: PostRepository.GetAllCallBack<Unit>) {
PostsApi.retrofitService.removeById(id).enqueue(object :Callback<Unit>{
    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
        if (!response.isSuccessful) {
            callback.onError(RuntimeException(response.message()))
            return
        }
        callback.onSuccess(close())

    }

    override fun onFailure(call: Call<Unit>, t: Throwable) {
      callback.onError(Exception(t))
    }

})
    }
}
