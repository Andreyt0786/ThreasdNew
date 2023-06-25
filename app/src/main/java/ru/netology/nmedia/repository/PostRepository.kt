package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.viewmodel.IdenticViewModel
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<FeedItem>>
    suspend fun getAll()
    suspend fun likeById(post: Post)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    fun getNewer(id:Long): Flow<Int>
    suspend fun updateDao()
    suspend fun saveWithAttachment(file:File, post:Post)
    suspend fun getToken(login:String?, password:String?): AuthModel


}
