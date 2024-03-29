package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun likeById(post: Post)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)


}
