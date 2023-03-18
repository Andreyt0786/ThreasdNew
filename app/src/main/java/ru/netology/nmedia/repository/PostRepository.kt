package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(callBack: GetAllCallBack<List<Post>>)
    fun likeById(post: Post,callBack: GetAllCallBack<Post>)
    fun save(post: Post,callBack: GetAllCallBack<Unit>)
    fun removeById(id: Long,callBack: GetAllCallBack<Unit>)

    interface GetAllCallBack<T>{
        fun onSuccess(data: T)
        fun onError(e: Exception)
    }
}
