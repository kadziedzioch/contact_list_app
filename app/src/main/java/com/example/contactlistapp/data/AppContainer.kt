package com.example.contactlistapp.data

import android.content.Context

interface AppContainer{
    val contactRepository: ContactRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer{

    override val contactRepository: ContactRepository by lazy {
        DefaultContactRepository(ContactDatabase.getDatabase(context).contactDao())
    }
}

