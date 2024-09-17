package com.example.demoapp

import android.content.Context
import androidx.room.Room
import com.example.demoapp.model.api.ApiService
import com.example.demoapp.model.api.MarvelApiRepo
import com.example.demoapp.model.db.CharacterDao
import com.example.demoapp.model.db.CollectionDb
import com.example.demoapp.model.db.CollectionDbRepo
import com.example.demoapp.model.db.CollectionDbRepoImpl
import com.example.demoapp.model.db.Constants.DB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {

    @Provides
    fun provideApiRepo() = MarvelApiRepo(ApiService.api)

    @Provides
    fun provideCollectionDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, CollectionDb::class.java, DB).build()

    @Provides
    fun provideCharacterDao(collectionDb: CollectionDb): CharacterDao = collectionDb.characterDao()

//    @Provides
//    fun provideCharactersDb(collectionDb: CollectionDb): CharacterDao = collectionDb.characterDao()

    @Provides
    fun provideDbRepoImpl(characterDao: CharacterDao) : CollectionDbRepo = CollectionDbRepoImpl(characterDao)
}