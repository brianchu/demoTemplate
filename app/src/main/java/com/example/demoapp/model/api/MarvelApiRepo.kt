package com.example.demoapp.model.api

import androidx.compose.runtime.mutableStateOf
import com.example.demoapp.model.CharacterResult
import com.example.demoapp.model.CharactersApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarvelApiRepo(
    private val api: MarvelApi
) {
    var characters = MutableStateFlow<NetworkResult<CharactersApiResponse>>(NetworkResult.Initial())
    var characterDetails = mutableStateOf<CharacterResult?>(null)

    fun query(query: String) {
        characters.value = NetworkResult.Loading()

        api.getCharacters(query)
            .enqueue(object : Callback<CharactersApiResponse> {
                override fun onFailure(call: Call<CharactersApiResponse>, t: Throwable) {
                    characters.value = NetworkResult.Error(t.localizedMessage ?: "unknown err")
                }

                override fun onResponse(
                    call: Call<CharactersApiResponse>,
                    response: Response<CharactersApiResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            characters.value = NetworkResult.Success(it)
                        }
                    } else {
                        characters.value = NetworkResult.Error(response.message())
                    }
                }
            })
    }

    fun getSingleCharacter(id: Int?) {
        id?.let {
            characterDetails.value = characters.value.data?.data?.results?.firstOrNull { result ->
                result.id == id
            }
        }
    }
}