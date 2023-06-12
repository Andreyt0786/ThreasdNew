package ru.netology.nmedia.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.model.AuthModel
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
) : PostRepository {

    override val data = Pager(
        config= PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
                PostRemoteMediator(
                    apiService
        )}
    ).flow
        //postDao.getAll().map { it.map(PostEntity::toDto) }.flowOn(Dispatchers.Default)

    override suspend fun updateDao() {
        postDao.updatePostsFromDao()
    }

    override suspend fun getToken(login: String?, password: String?): AuthModel {
        val response = apiService.updateUser(login, password)

        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return response.body() ?: throw ApiError(response.code(), response.message())
    }


    override suspend fun saveWithAttachment(file: File, post: Post) {
        val media = upload(file)
        val posts = apiService.save(
            post.copy(
                attachment = Attachment(
                    url = media.id,
                    type = AttachmentType.IMAGE
                )
            )
        )
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        postDao.insert(PostEntity.fromDto(body))

    }

    private suspend fun upload(file: File): Media {
        return apiService.upload(
            MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        )
            .let { requireNotNull(it.body()) }
    }


    override suspend fun getAll() {
        val posts = apiService.getAll()
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
            val posts = apiService.getNewer(id)
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
            val posts = apiService.likeById(post.id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
            postDao.insert(PostEntity.fromDto(body))
        } else {
            val posts = apiService.dislikeById(post.id)
            if (!posts.isSuccessful) {
                throw ApiError(posts.code(), posts.message())
            }
            val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
            postDao.insert(PostEntity.fromDto(body))
        }
    }

    override suspend fun save(post: Post) {

        val posts = apiService.save(post)
        if (!posts.isSuccessful) {
            throw ApiError(posts.code(), posts.message())
        }
        val body = posts.body() ?: throw ApiError(posts.code(), posts.message())
        postDao.insert(PostEntity.fromDto(body))

    }

    override suspend fun removeById(id: Long) {
        val posts = apiService.removeById(id)
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
    Api.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
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
        Api.retrofitService.likeById(post.id).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.message()))
                    return
                }
                callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
            }
 if (!post.likedByMe) {
        Api.retrofitService.likeById(post.id).enqueue(object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }

        }
        )
    } else {
        Api.retrofitService.dislikeById(post.id).enqueue(object : Callback<Post> {
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
    Api.retrofitService.save(post).enqueue(object : Callback<Unit> {
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
Api.retrofitService.removeById(id).enqueue(object :Callback<Unit>{
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
