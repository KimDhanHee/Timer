package damin.tothemoon.timer.ui.componenets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import damin.tothemoon.timer.R
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.model.timeStr
import damin.tothemoon.timer.viewmodel.TimerListViewModel

@Composable
fun TimerInfoListScreen(
  timerViewModel: TimerListViewModel = viewModel(),
  onTimerInfoClick: (TimerInfo) -> Unit = {},
) {
  Column(
    modifier = Modifier.padding(20.dp)
  ) {
    Spacer(modifier = Modifier.size(4.dp))

    Header()

    Spacer(modifier = Modifier.size(24.dp))

    val timerInfoList by timerViewModel.timerListFlow.collectAsState()
    TimerInfoList(timerInfoList, onTimerInfoClick)
  }
}

@Composable
private fun Header(
  onDeleteClick: () -> Unit = {},
  onAddClick: () -> Unit = {},
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Text(
      text = stringResource(id = R.string.app_name),
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.headlineLarge,
    )

    IconButton(onClick = onDeleteClick) {
      Icon(painter = painterResource(id = R.drawable.ic_minus_48), contentDescription = null)
    }

    Spacer(modifier = Modifier.size(4.dp))

    IconButton(onClick = onAddClick) {
      Icon(painter = painterResource(id = R.drawable.ic_plus_48), contentDescription = null)
    }
  }
}

@Composable
private fun TimerInfoList(
  timerInfoList: List<TimerInfo>,
  onTimerInfoClick: (TimerInfo) -> Unit = {},
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(timerInfoList, key = { it.id }) { timerInfo ->
      TimerListItem(timerInfo, onTimerClick = { onTimerInfoClick(timerInfo) })
    }
  }
}

@Composable
private fun TimerListItem(
  timerInfo: TimerInfo,
  modifier: Modifier = Modifier,
  onTimerClick: () -> Unit = {},
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onTimerClick() },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = Color(timerInfo.color.src)
    )
  ) {
    Column(
      modifier = Modifier
        .padding(20.dp)
    ) {
      Text(
        text = timerInfo.title,
        style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
      )
      Spacer(modifier = Modifier.size(8.dp))
      Text(
        text = timerInfo.time.timeStr,
        style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
      )
    }
  }
}
