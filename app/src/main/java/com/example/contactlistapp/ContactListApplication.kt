package com.example.contactlistapp

import android.app.Application
import com.example.contactlistapp.data.AppContainer
import com.example.contactlistapp.data.DefaultAppContainer

class ContactListApplication : Application() {

    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}