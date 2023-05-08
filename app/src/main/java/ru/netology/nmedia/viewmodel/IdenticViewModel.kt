package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.identic.Identic
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.IdenticModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

class IdenticViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())


    private val _tokenServer = SingleLiveEvent<AuthModelState>()
    val tokenServer: LiveData<AuthModelState>
        get() = _tokenServer


    fun getIdToken(login: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.getToken(login, password)
                response.token?.let {
                    AppAuth.getInstance().setUser(AuthModel(response.id, response.token))
                    _tokenServer.value = AuthModelState(firstView = false, complete = true)
                }
            } catch (e: ApiError) {
                _tokenServer.value = AuthModelState(firstView = false, errorApi = true)
            } catch (e: Exception) {
                _tokenServer.value = AuthModelState(firstView = false, error = true)
            }
        }
    }
}


