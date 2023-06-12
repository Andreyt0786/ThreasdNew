package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import retrofit2.HttpException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import java.io.IOException
import java.security.PrivateKey

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val postDao: PostDao,
) : RemoteMediator<Int, Post>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Post>): MediatorResult {

        try {
            val result = when (loadType) {
                LoadType.REFRESH -> {
                    apiService.getLatest(state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = state.firstItemOrNull()?.id?:return MediatorResult.Success(false)
                    apiService.getAfter(id , state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = state.lastItemOrNull()?.id ?: return MediatorResult.Success(false)
                    apiService.getBefore(id, state.config.pageSize)
                }
            }
            if (!result.isSuccessful) {
                throw HttpException(result)
            }
            val body = result.body()?:throw ApiError(
                result.code(),
                result.message()
            )
            val data = result.body().orEmpty()

            postDao.insert(body.map(PostEntity::fromDto))
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }


}