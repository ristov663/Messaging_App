package com.example.di

import com.example.data.repos.ChatRoomRepositoryImpl
import com.example.data.repos.MessageRepositoryImpl
import com.example.data.repos.UserRepositoryImpl
import com.example.domain.repos.ChatRoomRepository
import com.example.domain.repos.MessageRepository
import com.example.domain.repos.UserRepository
import org.koin.dsl.module

val reposModule = module {

    single<UserRepository> {
        UserRepositoryImpl()
    }

    single<MessageRepository> {
        MessageRepositoryImpl()
    }

    single<ChatRoomRepository> {
        ChatRoomRepositoryImpl()
    }
}
