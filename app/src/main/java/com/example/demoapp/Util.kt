package com.example.demoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.math.BigInteger
import java.security.MessageDigest

fun getHash(timeStamp: String, privateKey: String, publicKey: String): String {
    val result: ByteArray = MessageDigest.getInstance("MD5").digest(
        (timeStamp + privateKey + publicKey).toByteArray()
    )
    return BigInteger(1, result).toString(16).toString().padStart(32, '0')
}

@Composable
fun AttributionText(text: String) {
    Text(text = text, modifier = Modifier.padding(start = 8.dp, top = 4.dp), fontSize = 12.sp)
}

@Composable
fun CharacterImage(
    url: String?,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.FillHeight
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}

fun List<String>.comicsToString(): String {
    return joinToString(separator = ", ")
}

// new log trick
fun <T: Any>T.tag() :String =  this::class.java.toString()