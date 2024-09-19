package com.example.demoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.model.CharacterResult
import com.example.demoapp.model.db.CollectionDbRepo
import com.example.demoapp.model.db.DbCharacter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionDbViewModel @Inject constructor(private val repo: CollectionDbRepo)  : ViewModel() {
    val currentCharacter = MutableStateFlow<DbCharacter?>(null)
    val collection = MutableStateFlow<List<DbCharacter>>(listOf())

    init {
        getCollection()
    }

    private fun getCollection() {
        viewModelScope.launch {
            repo.getCharactersFromRepo().collect {
                collection.value = it
            }
        }
    }

    fun setCurrentCharacterId(characterId: Int?)  {
        characterId?.let {
           viewModelScope.launch {
               repo.getCharacterFromRepo(it).collect { character ->
                   currentCharacter.value = character
               }
           }
        }
    }

    fun addCharacter(character: CharacterResult) {
        viewModelScope.launch(Dispatchers.IO) {
           repo.addCharacterToRepo(DbCharacter.fromCharacter(character))
        }
    }

    fun deleteCharacter(character: DbCharacter) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteCharacterFromRepo(character)
        }
    }
}