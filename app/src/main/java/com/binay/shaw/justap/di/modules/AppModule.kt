package com.binay.shaw.justap.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppContext(app: Application): Context {
        return app.applicationContext
    }

//    @Provides
//    @Singleton
//    fun provideSharedPreferences(app: Application): SharedPreferences {
//        return app.getSharedPreferences("Settings", Context.MODE_PRIVATE)
//    }
}
