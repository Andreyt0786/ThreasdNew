package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class AuthModelState(
    val firstView: Boolean = false,
    val errorApi: Boolean = false,
    val error: Boolean = false,
    val complete:Boolean = false,
)

