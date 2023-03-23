package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = "",
    authorAvatar = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAll(object : PostRepository.GetAllCallBack<List<Post>> {
            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    /*thread {
        // Начинаем загрузку
        _data.postValue(FeedModel(loading = true))
        try {
            // Данные успешно получены
            val posts = repository.getAll()
            FeedModel(posts = posts, empty = posts.isEmpty())
        } catch (e: IOException) {
            // Получена ошибка
            FeedModel(error = true)
        }.also(_data::postValue)
    }*/


    fun save() {
        edited.value?.let {
            /* thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }*/
            repository.save(it, object : PostRepository.GetAllCallBack<Unit> {
                override fun onSuccess(data: Unit) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }


            })
        }
        edited.value = empty
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
        repository.likeById(post, object : PostRepository.GetAllCallBack<Post> {
            /*thread {
            val likedPost = repository.likeById(post)
            _data.postValue(
                FeedModel(
                    posts = _data.value?.posts.orEmpty()
                        .map { if (it.id == post.id) likedPost else it })
            )
        }*/
            override fun onSuccess(data: Post) {
                _data.postValue(
                    FeedModel(
                        posts = _data.value?.posts.orEmpty()
                            .map { if (it.id == post.id) data else it })
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun removeById(id: Long) {
        /*  thread {
              // Оптимистичная модель
              val old = _data.value?.posts.orEmpty()
              _data.postValue(
                  _data.value?.copy(posts = _data.value?.posts.orEmpty()
                      .filter { it.id != id }
                  )
              )
              try {
                  repository.removeById(id)
              } catch (e: IOException) {
                  _data.postValue(_data.value?.copy(posts = old))
              }
          }*/
        repository.removeById(id, object : PostRepository.GetAllCallBack<Unit> {
            override fun onSuccess(data: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }

        })
    }
}




