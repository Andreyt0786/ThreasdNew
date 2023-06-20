package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.FeedPosts
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = "",
    authorAvatar = "",
    attachment = null,
    authorId = 0L,
)

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {
    // упрощённый вариант


    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    val data: Flow<PagingData<Post>> = appAuth.authStateFlow.flatMapLatest { (id, _) ->
        repository.data
            .map { posts ->
                posts.map { it.copy(ownedByMe = it.authorId == id) }
            }
    }.flowOn(Dispatchers.Default)

    private val _photoState = MutableLiveData<PhotoModel?>()
    val photoState: LiveData<PhotoModel?>
        get() = _photoState

    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    var lastId = 0L
 /*   val newer: LiveData<Int> = data.switchMap {
        lastId = it.posts.lastOrNull()?.id ?: 0L
        repository.getNewer(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)

    }*/

    init {
        loadPosts()
    }

    fun updateFromDao() = viewModelScope.launch {
        repository.updateDao()

    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _state.value = FeedModelState(loading = true)
            repository.getAll()
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _state.value = FeedModelState(refreshing = true)
            val posts = repository.getAll()
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }
    }

    fun changePhoto(photoModel: PhotoModel?) {
        _photoState.value = photoModel
    }
    /*thread {
        // Начинаем загрузку
        _state.postValue(FeedModelState(loading = true))
        try {
            // Данные успешно получены
            val posts = repository.getAll()
            FeedModelState(posts = posts, empty = posts.isEmpty())
        } catch (e: IOException) {
            // Получена ошибка
            FeedModelState(error = true)
        }.also(_state::postValue)
    }*/


    fun save() {
        edited.value?.let { post ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    _photoState.value?.let { photoModel ->
                        repository.saveWithAttachment(photoModel.file, post)
                    } ?: repository.save(post)
                    _state.value = FeedModelState()
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = true)
                }

            }
            _photoState.value = null
            edited.value = empty
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {
        viewModelScope.launch {
            repository.likeById(post)
            _state.value = FeedModelState()
        }
        /*repository.likeById(post, object : PostRepository.GetAllCallBack<Post> {
        override fun onSuccess(data: Post) {
            _state.postValue(
                FeedModelState(
                    posts = _state.value?.posts.orEmpty()
                        .map { if (it.id == post.id) data else it })
            )
        }

        override fun onError(e: Exception) {
            _state.postValue(FeedModelState(error = true))
        }
    })*/

        /*thread {
       val likedPost = repository.likeById(post)
       _state.postValue(
           FeedModelState(
               posts = _state.value?.posts.orEmpty()
                   .map { if (it.id == post.id) likedPost else it })
       )
   }*/
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }
}





/*  thread {
  // Оптимистичная модель
  val old = _state.value?.posts.orEmpty()
  _state.postValue(
      _state.value?.copy(posts = _state.value?.posts.orEmpty()
          .filter { it.id != id }
      )
  )
  try {
      repository.removeById(id)
  } catch (e: IOException) {
      _state.postValue(_state.value?.copy(posts = old))
  }
}*/


/*repository.removeById(id, object : PostRepository.GetAllCallBack<Unit> {
override fun onSuccess(data: Unit) {
    _state.postValue(
        _state.value?.copy(posts = _state.value?.posts.orEmpty()
            .filter { it.id != id }
        )
    )
}

override fun onError(e: Exception) {
    _state.postValue(FeedModelState(error = true))
}

})
}*/




