package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class IdenticViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    private val _tokenServer = SingleLiveEvent<AuthModelState>()
    val tokenServer: LiveData<AuthModelState>
        get() = _tokenServer


    fun getIdToken(login: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.getToken(login, password)
                response.token?.let {
                    appAuth.setUser(AuthModel(response.id, response.token))
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


