package com.example.demoapp.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.demoapp.comicsToString
import com.example.demoapp.model.CharacterResult
import com.example.demoapp.model.db.Constants.CHARACTER_TABLE


@Entity(tableName = CHARACTER_TABLE)
data class DbCharacter(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val apiId: Int?,
    val name: String?,
    val thumbnail: String?,
    val comics: String?,
    val description: String?
) {
   companion object {
       fun fromCharacter(character: CharacterResult) =
           DbCharacter(
               id = 0,
               apiId = character.id,
               name = character.name,
               thumbnail = character.thumbnail?.path + "." + character.thumbnail?.extension,
               comics = character.comics?.items?.mapNotNull { it.name }?.comicsToString() ?: "no comics",
               description = character.description
           )
   }
}