package ru.netology.nmedia.identic

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.AuthModel
import ru.netology.nmedia.model.IdenticModel

class Identic private constructor(context: Context) {

    private val idenPrefs = context.getSharedPreferences("iden", Context.MODE_PRIVATE)
    private val _idenStateFlow: MutableStateFlow<IdenticModel>


    init {
        val login = idenPrefs.getString(LOGIN_KEY, null)
        val pass= idenPrefs.getString(PASS_KEY, null)

        if (login == null || pass == null) {
            idenPrefs.edit { clear() }
            _idenStateFlow = MutableStateFlow(IdenticModel())
        } else {
            _idenStateFlow = MutableStateFlow(IdenticModel(login,pass))
        }
    }

    val idenStateFlow = _idenStateFlow.asStateFlow()

    fun setIdentific(identif: IdenticModel) {
        _idenStateFlow.value = identif
        idenPrefs.edit {
            putString(LOGIN_KEY, identif.login)
            putString(PASS_KEY, identif.pass)
        }
    }

    fun removeUser() {
        _idenStateFlow.value = IdenticModel()
        idenPrefs.edit { clear() }
    }

    companion object {
        private const val LOGIN_KEY = "LOGIN_KEY"
        private const val PASS_KEY = "PASS_KEY"

        @Volatile
        private var instance: Identic? = null

        @Synchronized
        fun initAppAuth(context: Context):Identic {
            return instance ?: Identic(context).apply { instance = this }
        }

        fun getInstance(): Identic = requireNotNull(instance) { "identification was not invoke" }
    }

}
