package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.identic.Identic
import ru.netology.nmedia.model.IdenticModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class IdenticViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())

    val identicLiveData = Identic.getInstance().idenStateFlow.asLiveData(Dispatchers.Default)

    val login: String?
        get() = Identic.getInstance().idenStateFlow.value.login

    val password: String?
        get() = Identic.getInstance().idenStateFlow.value.pass

    fun getIdToken() {
        viewModelScope.launch {
            identicLiveData.value?.let {
                repository.getToken(login, password)
            }
        }
    }
}

