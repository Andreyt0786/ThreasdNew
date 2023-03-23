package ru.netology.nmedia.repository

import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.platform.android.AndroidLogHandler.close
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://192.168.0.17:9090"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callback: PostRepository.GetAllCallBack<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun likeById(post: Post, callback: PostRepository.GetAllCallBack<Post>) {

        if (!post.likedByMe) {
            val request: Request = Request.Builder()
                .post("".toRequestBody(null))
                .url("${BASE_URL}/api/posts/${post.id}/likes")
                .build()

            return client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(gson.fromJson(body, Post::class.java))
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                })

            /* .execute()
             .let { it.body?.string() ?: throw RuntimeException("body is null") }
             .let {
                 gson.fromJson(it, Post::class.java)
             }*/

        } else {
            val request: Request = Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/${post.id}/likes")
                .build()

            return client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string() ?: throw RuntimeException("body is null")
                        try {
                            callback.onSuccess(gson.fromJson(body, Post::class.java))
                        } catch (e: Exception) {
                            callback.onError(e)
                        }
                    }
                })
            /*  .execute()
              .let { it.body?.string() ?: throw RuntimeException("body is null") }
              .let {
                  gson.fromJson(it, Post::class.java)

              }*/
        }
    }

    override fun save(post: Post,callback: PostRepository.GetAllCallBack<Unit>) {
        val request: Request = Request.Builder()
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
    }

    override fun removeById(id: Long,callback: PostRepository.GetAllCallBack<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            /*.execute()
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
    }
}
