package com.example.adere

import android.app.Application
import com.example.adere.core.common.AdereAppContainer

class AdereApplication : Application() {
    lateinit var container: AdereAppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AdereAppContainer(this)
    }
}
