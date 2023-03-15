package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    //fun getAll(callBack: GetAllCallBack<List<Post>>)
   // fun likeById(post: Post,callBack: GetAllCallBack<Post>)
    fun getAll():List<Post>
    fun likeById(post: Post):Post
    fun save(post: Post)
    fun removeById(id: Long)

    interface GetAllCallBack<T>{
        fun onSuccess(data: T)
        fun onError(e: Exception)
    }
}
