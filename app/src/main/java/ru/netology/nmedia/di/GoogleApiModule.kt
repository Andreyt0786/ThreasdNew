//package ru.netology.nmedia.di
/*
import android.content.Context
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)//база данных будет использоватья во всем приложении
@Module
class GoogleApiModule(
    appActivity: AppActivity,
    context: Context,
) {

    companion object {
        private var instance: GoogleApiModule? = null

        fun initApp(context: Context) {
            instance = GoogleApiModule(appActivity = AppActivity(),context)
        }

        fun getInstance():GoogleApiModule{
            return instance!!
        }

    }

    val googleApi =
        {
            with(GoogleApiAvailability.getInstance()) {
                val code = isGooglePlayServicesAvailable(appActivity)
                if (code == ConnectionResult.SUCCESS) {
                    return@with
                }
                if (isUserResolvableError(code)) {
                    getErrorDialog(appActivity, code, 9000)?.show()
                }
                Toast.makeText(appActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                    .show()
            }
            getToken
        }

    private val getToken = FirebaseMessaging.getInstance().token.addOnSuccessListener {
        println(it)
    }
}*/