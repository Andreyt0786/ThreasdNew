package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import okhttp3.internal.platform.android.AndroidLogHandler.close
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {

    override val data: LiveData<List<Post>> = postDao.getAll().map { it.map(PostEntity::toDto) }

    override suspend fun getAll() {
        val posts = PostsApi.retrofitService.getAll()
        if (!posts.isSuccessful) throw Exception("api is error")
       val body = posts.body() ?: throw Exception("body is null")
        //postDao.insert(posts.body()!!.map { PostEntity.fromDto(it) })
        postDao.insert(body.toEntity())
    }

    override suspend fun likeById(post: Post) {
        if (!post.likedByMe) {
           val posts =  PostsApi.retrofitService.likeById(post.id)
            if (!posts.isSuccessful) throw Exception("api is error")
            val body = posts.body() ?: throw Exception("body is null")
            postDao.insert(PostEntity.fromDto(body))
        } else{
            val posts = PostsApi.retrofitService.dislikeById(post.id)
            if (!posts.isSuccessful) throw Exception("api is error")
            val body = posts.body() ?: throw Exception("body is null")
            postDao.insert(PostEntity.fromDto(body))
        }
    }

    override suspend fun save(post: Post) {

        val posts = PostsApi.retrofitService.save(post)
        if (!posts.isSuccessful) throw Exception("api is error")
        val body = posts.body() ?: throw Exception("body is null")
        postDao.insert(PostEntity.fromDto(body))

    }

    override suspend fun removeById(id: Long) {
        val posts = PostsApi.retrofitService.removeById(id)
        if (!posts.isSuccessful) {
            throw Exception("api is error")
        } else { postDao.removeById(id)
            // val body = posts.body() ?: throw Exception("body is null")
            //postDao.insert(PostEntity.fromDto(body))
        }
    }
}


/* override fun getAll(callback: PostRepository.GetAllCallBack<List<Post>>) {
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
 if (!post.likedByMe) {
        PostsApi.retrofitService.likeById(post.id).enqueue(object : Callback<Post> {
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
}*/
