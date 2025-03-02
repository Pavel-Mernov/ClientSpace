package com.example.clientspace.ui

import android.content.Context
import com.example.clientspace.R
import java.time.LocalDateTime

object UserRepository {
    const val defaultUserId = "pavel_mernov"

    private fun getImageBytes(resId : Int, context : Context) : ByteArray {
        val inputStream = context.resources.openRawResource(resId)
        val byteArray = inputStream.readBytes()
        return byteArray
    }

    private lateinit var defaultUsers : MutableList<User>

    fun initialize(context : Context) {
        defaultUsers = listOf(
            User(
                defaultUserId,
                getImageBytes(R.drawable.ic_avatar_main, context),
                "012345",
                "Pavel Mernov",
                "Hello, my name is Pavel Mernov",
                listOf(
                    Chat(
                        chatId = 0,
                        avatarImage = getImageBytes(R.drawable.ic_avatar1, context),
                        name = "Сергей Виденин",
                        isForTwo = true,
                        otherMembers = listOf(
                            "sergey_videnin"
                        ).toMutableList(),
                        messages = listOf(
                            Message(
                                fromId = defaultUserId,
                                text = "Здравствуйте, Сергей!",
                                time = LocalDateTime.now().minusMinutes(10)
                            )
                        ).toMutableList()
                    ),
                    Chat(
                        chatId = 1,
                        avatarImage = getImageBytes(R.drawable.ic_avatar2, context),
                        name = "Алексей Сурков",
                        isForTwo = true,
                        otherMembers = listOf(
                            "alex_surkov"
                        ).toMutableList(),
                        messages = listOf(
                            Message(
                                fromId = defaultUserId,
                                text = "Ну что, решили задачу?",
                                time = LocalDateTime.now().minusMinutes(45)
                            )
                        ).toMutableList()
                    ),
                    Chat(
                        chatId = 2,
                        avatarImage = getImageBytes(R.drawable.ic_avatar3, context),
                        name = "Иван Давыдов",
                        isForTwo = true,
                        otherMembers = listOf(
                            "ivan_davydov"
                        ).toMutableList(),
                        messages = listOf(
                            Message(
                                fromId = defaultUserId,
                                text = "Как ваша отладка сервера?",
                                time = LocalDateTime.now().minusHours(2)
                            )
                        ).toMutableList()
                    ),
                    Chat(
                        chatId = 3,
                        avatarImage = getImageBytes(R.drawable.ic_avatar4, context),
                        name = "Полина Дроздова",
                        isForTwo = true,
                        otherMembers = listOf(
                            "p_drozdova"
                        ).toMutableList(),
                        messages = listOf(
                            Message(
                                fromId = "p_drozdova",
                                text = "Мне нужен синий фон",
                                reaction = Reaction.LIKE,
                                time = LocalDateTime.now().minusHours(3).minusMinutes(20)
                            )
                        ).toMutableList()
                    ),
                    Chat(
                        chatId = 4,
                        avatarImage = getImageBytes(R.drawable.ic_avatar5, context),
                        name = "Георгий Смирнов",
                        isForTwo = true,
                        otherMembers = listOf("george_smirnov").toMutableList(),
                        messages = listOf(
                            Message(
                                fromId = defaultUserId,
                                text = "Помогите, мне сложно!",
                                time = LocalDateTime.now().minusHours(4).minusMinutes(30)
                            )
                        ).toMutableList()
                    )
                ).toMutableList()
            )
        ).toMutableList()
    }

    /*
    fun getDefaultUser(context : Context) : User {
        return defaultUsers.first()
    }
     */

    fun findUserById(id : String) : User? {
        return defaultUsers.firstOrNull { it.userId == id }
    }

    fun updateUser(newUser: User) {
        defaultUsers.removeAll{ it.userId == newUser.userId }
        defaultUsers.add(newUser)
    }
}