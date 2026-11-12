package com.example.tutorlink

import android.app.Application
import com.example.tutorlink.di.AppContainer

class TutorLinkApplication : Application() {
    lateinit var container: AppContainer private set
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}