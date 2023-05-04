package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvatar:String,
    @Embedded
    val attachment: Attachment?,
    val hidden: Boolean =false,
) {
    fun toDto() = Post(
        id = id,
        author = author,
        authorId = authorId,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        authorAvatar = authorAvatar,
        attachment = attachment
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                id = dto.id,
                author = dto.author,
                authorId = dto.authorId,
                content = dto.content,
                published = dto.published,
                likedByMe = dto.likedByMe,
                likes = dto.likes,
                authorAvatar = dto.authorAvatar,
                attachment = dto.attachment
            )

    }
}
fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)

