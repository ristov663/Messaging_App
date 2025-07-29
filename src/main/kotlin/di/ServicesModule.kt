package com.example.di

import com.example.data.services.AIAgentServiceImpl
import com.example.data.services.ChatRoomServiceImpl
import com.example.data.services.MessageServiceImpl
import com.example.data.services.UserServiceImpl
import com.example.domain.services.AIAgentService
import com.example.domain.services.ChatRoomService
import com.example.domain.services.MessageService
import com.example.domain.services.UserService
import org.koin.dsl.module

val servicesModule = module {

    single<UserService> {
        UserServiceImpl(get())
    }

    single<MessageService> {
        MessageServiceImpl(get())
    }

    single<ChatRoomService> {
        ChatRoomServiceImpl(get())
    }

    single<AIAgentService> {
        AIAgentServiceImpl(get())
    }
}
