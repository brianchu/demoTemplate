package com.example.demoapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demoapp.viewmodel.RewardedAdViewModel

@Composable
fun RewardedAd(rewardedAdViewModel: RewardedAdViewModel) {

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White)
            .padding(4.dp)
            .wrapContentHeight(),
        onClick = {
            rewardedAdViewModel.showRewardedAd {  }
        }
    ) {
        Text(
            text = "Reward Ad",
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
