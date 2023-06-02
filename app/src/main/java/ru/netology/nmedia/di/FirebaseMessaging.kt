package ru.netology.nmedia.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.google.firebase.messaging.FirebaseMessaging

@InstallIn(SingletonComponent::class)
@Module
class FirebaseMessaging {

    @Provides
    fun firebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}