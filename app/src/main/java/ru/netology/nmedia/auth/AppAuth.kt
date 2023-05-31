package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.model.AuthModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private  val context: Context,

) {//inject  создает и предоставляет класс в другие классы

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authStateFlow: MutableStateFlow<AuthModel>


    init {
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0)

        if (id == 0L || token == null) {
            prefs.edit { clear() }
            _authStateFlow = MutableStateFlow(AuthModel())
        } else {
            _authStateFlow = MutableStateFlow(AuthModel(id, token))
        }

        uploadPushToken()
    }

    val authStateFlow = _authStateFlow.asStateFlow()

    @InstallIn(SingletonComponent::class)//чтобы обратитьяс к графу и получить ссылку на объект
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService
    }


    fun uploadPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
                entryPoint.getApiService().uploadPushToken(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setUser(user: AuthModel) {
        _authStateFlow.value = user
        prefs.edit {
            putLong(ID_KEY, user.id)
            putString(TOKEN_KEY, user.token)
        }
        uploadPushToken()
    }

    fun removeUser() {
        _authStateFlow.value = AuthModel()
        prefs.edit { clear() }
        uploadPushToken()
    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"
    }


}