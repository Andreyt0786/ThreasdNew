package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import java.io.File

class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {

    override val data =
        postDao.getAll().map { it.map(PostEntity::toDto) }.flowOn(Dispatchers.Default)

    override fun updateDao() {
        postDao.updatePostsFromDao()
    }

    override suspend fun saveWithAttachment(file: File, post: Post) {
        val media = upload(file)
        val posts = PostsApi.retrofitService.save(post.copy(attachment = Attachment(url=media.id,type = AttachmentType.IMAGE)))
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        postDao.insert(PostEntity.fromDto(body))

    }

    private suspend fun upload(file: File): Media {
        return PostsApi.retrofitService.upload(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody()))
                .let { requireNotNull(it.body())}
    }


    override suspend fun getAll() {
        val posts = PostsApi.retrofitService.getAll()
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        //postDao.insert(posts.body()!!.map { PostEntity.fromDto(it) })
        postDao.insert(body.toEntity())
    }

    override fun getNewer(id: Long) = flow {
        while (true) {
            delay(10000)
            val posts = PostsApi.retrofitService.getNewer(id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: emptyList()
            emit(body.size)
            postDao.insert(body.toEntity().map { it.copy(hidden = true) })
        }
    }.flowOn(Dispatchers.Default)


    override suspend fun likeById(post: Post) {
        if (!post.likedByMe) {
            val posts = PostsApi.retrofitService.likeById(post.id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
            postDao.insert(PostEntity.fromDto(body))
        } else {
            val posts = PostsApi.retrofitService.dislikeById(post.id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
            postDao.insert(PostEntity.fromDto(body))
        }
    }

    override suspend fun save(post: Post) {

        val posts = PostsApi.retrofitService.save(post)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        postDao.insert(PostEntity.fromDto(body))

    }

    override suspend fun removeById(id: Long) {
        val posts = PostsApi.retrofitService.removeById(id)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        } else {
            postDao.removeById(id)
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
