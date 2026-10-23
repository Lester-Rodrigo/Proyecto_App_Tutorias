package com.example.app_tutorias

import android.app.Application
import com.example.app_tutorias.di.AppContainer

class TutoriasApplication : Application() {
    lateinit var container: AppContainer private set
    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}