package com.example.clientspace.ui

import android.content.Context
import com.example.clientspace.R
import java.time.LocalDateTime

object UserRepository {
    private const val currentUserId = "pavel_mernov"

    private fun getImageBytes(resId : Int, context : Context) : ByteArray {
        val inputStream = context.resources.openRawResource(resId)
        val byteArray = inputStream.readBytes()
        return byteArray
    }

    private lateinit var defaultUsers : MutableList<User>

    fun initialize(context : Context) {
        defaultUsers = listOf(
            User(
                "p_drozdova",
                getImageBytes(R.drawable.ic_avatar4, context),
                userName = "Полина Дроздова",
                description = "Графический дизайнер. Котики моё всё:)"
            ),
            User(
                "sergey_videnin",
                getImageBytes(R.drawable.ic_avatar1, context),
                userName = "Сергей Виденин",
            ),
            User(
                "ivan_davydov",
                getImageBytes(R.drawable.ic_avatar3, context),
                userName = "Иван Давыдов",
                description = "Всем привет. Меня зовут Иван Давыдов. Я учусь в НИУ ВШЭ"
            ),
            User(
                "alex_surkov",
                getImageBytes(R.drawable.ic_avatar2, context),
                userName = "Алексей Сурков",
                description = "Студент ФКН ПИ ВШЭ"
            ),
            User(
                "george_smirnov",
                getImageBytes(R.drawable.ic_avatar5, context),
                userName = "Георгий Смирнов",
            ),
            User(
                currentUserId,
                getImageBytes(R.drawable.ic_avatar_main, context),
                "012345".toByteArray(),
                "Павел Мернов",
                "Hello, my name is Pavel Mernov",
                isCurrent = true,
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
                                fromId = currentUserId,
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
                                fromId = currentUserId,
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
                                fromId = currentUserId,
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
                                fromId = currentUserId,
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

    fun getCurrentUser() : User? {
        return findUserById(currentUserId)
    }

    fun findUserById(id : String) : User? {
        return defaultUsers.firstOrNull { it.userId == id }
    }

    fun updateUser(newUser: User) {
        defaultUsers.removeAll{ it.userId == newUser.userId }
        defaultUsers.add(newUser)
    }
}