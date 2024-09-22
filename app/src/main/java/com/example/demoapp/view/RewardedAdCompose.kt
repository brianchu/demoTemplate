package com.example.demoapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demoapp.viewmodel.RewardedAdViewModel

@Preview
@Composable
fun RewardedAdCompose() {

    /**
     * Option for rewarded ad, since it is full screen, all we need to do is
     * ask view model to show it when user click on the button
     */
    val rewardedAdViewModel: RewardedAdViewModel = hiltViewModel()

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White)
            .padding(4.dp)
            .wrapContentHeight(),
        onClick = {
            rewardedAdViewModel.showRewardedAd()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = "Unlock more result:",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = "Reward Ad",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
