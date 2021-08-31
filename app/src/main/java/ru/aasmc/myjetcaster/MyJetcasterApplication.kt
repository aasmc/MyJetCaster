package ru.aasmc.myjetcaster

import android.app.Application

class MyJetcasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}