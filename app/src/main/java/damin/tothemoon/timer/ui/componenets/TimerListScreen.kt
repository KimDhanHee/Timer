package damin.tothemoon.timer.ui.componenets

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import damin.tothemoon.timer.R
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.model.timeStr
import damin.tothemoon.timer.viewmodel.TimerListViewModel

@Composable
fun TimerInfoListScreen(
  timerListViewModel: TimerListViewModel = viewModel(),
  onClickAdd: () -> Unit,
  onClickTimerInfo: (TimerInfo) -> Unit,
) {
  val systemUiController = rememberSystemUiController()

  LaunchedEffect(Unit) {
    systemUiController.setSystemBarsColor(color = Color.White)
  }

  Column {
    Spacer(modifier = Modifier.size(4.dp))

    var deletable by rememberSaveable { mutableStateOf(false) }
    Header(
      deletable,
      onAddClick = onClickAdd,
      onDeleteClick = {
        deletable = true
      },
      onBackClick = {
        deletable = false
      }
    )

    Spacer(modifier = Modifier.size(24.dp))

    val timerInfoList by timerListViewModel.timerListFlow.collectAsState()
    TimerInfoList(
      timerInfoList,
      deletable,
      onClickTimerInfo,
      onTimerDeleteClick = { timerInfo -> timerListViewModel.deleteTimerInfo(timerInfo) }
    )

    BackHandler(enabled = deletable) {
      deletable = false
    }
  }
}

@Composable
private fun Header(
  isDeletable: Boolean = false,
  onDeleteClick: () -> Unit,
  onAddClick: () -> Unit,
  onBackClick: () -> Unit,
) {
  Crossfade(isDeletable) { deletable ->
    when {
      deletable -> DeletableStateHeader(onBackClick)
      else -> IdleStateHeader(onDeleteClick, onAddClick)
    }
  }
}

@Composable
private fun DeletableStateHeader(
  onBackClick: () -> Unit,
) {
  Icon(
    painter = painterResource(id = R.drawable.ic_back_48),
    contentDescription = null,
    modifier = Modifier
      .padding(start = 16.dp, end = 20.dp)
      .clickable { onBackClick() }
  )
}

@Composable
private fun IdleStateHeader(
  onDeleteClick: () -> Unit = {},
  onAddClick: () -> Unit = {},
) {
  Row(
    modifier = Modifier.padding(start = 20.dp, end = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = stringResource(id = R.string.app_name),
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.headlineLarge,
    )
    Icon(
      painter = painterResource(id = R.drawable.ic_minus_48),
      contentDescription = null,
      modifier = Modifier.clickable { onDeleteClick() }
    )
    Spacer(modifier = Modifier.size(4.dp))
    Icon(
      painter = painterResource(id = R.drawable.ic_plus_48),
      contentDescription = null,
      modifier = Modifier.clickable { onAddClick() }
    )
  }
}

@Composable
private fun TimerInfoList(
  timerInfoList: List<TimerInfo>,
  isDeletable: Boolean = false,
  onTimerInfoClick: (TimerInfo) -> Unit = {},
  onTimerDeleteClick: (TimerInfo) -> Unit = {},
) {
  LazyColumn(
    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(timerInfoList, key = { it.id }) { timerInfo ->
      TimerListItem(
        timerInfo,
        isDeletable = isDeletable,
        onTimerClick = { onTimerInfoClick(timerInfo) },
        onTimerDeleteClick = { onTimerDeleteClick(timerInfo) }
      )
    }
  }
}

@Composable
private fun TimerListItem(
  timerInfo: TimerInfo,
  modifier: Modifier = Modifier,
  isDeletable: Boolean = false,
  onTimerClick: () -> Unit = {},
  onTimerDeleteClick: () -> Unit = {},
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
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = timerInfo.title,
          modifier = Modifier.weight(1f),
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
        )
        AnimatedVisibility(
          isDeletable,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Icon(
            painter = painterResource(id = R.drawable.ic_delete_20),
            contentDescription = null,
            modifier = Modifier.clickable { onTimerDeleteClick() },
            tint = Color.White
          )
        }
      }
      Spacer(modifier = Modifier.size(8.dp))
      Text(
        text = timerInfo.time.timeStr,
        style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
      )
    }
  }
}
