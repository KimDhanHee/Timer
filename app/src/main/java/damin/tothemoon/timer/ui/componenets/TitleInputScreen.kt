package damin.tothemoon.timer.ui.componenets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import damin.tothemoon.timer.R

@Composable
fun TitleInputScreen(
  prevTitle: String,
  color: Int,
  navController: NavHostController,
) {
  var title by remember { mutableStateOf(prevTitle) }
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(color = Color(color))
      .padding(start = 16.dp, end = 16.dp, top = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Header(onClickBack = { navController.navigateUp() })
    Spacer(modifier = Modifier.size(92.dp))
    TitleTextField(title = title, onTitleChange = { title = it })
    Spacer(modifier = Modifier.size(124.dp))
    CtaButton(
      text = stringResource(id = R.string.timer_editor_save_title),
      enabled = title.isNotBlank(),
      onClick = {
        navController.previousBackStackEntry?.savedStateHandle?.set(
          TimerDestination.TimerEditor.TitleInput.KEY_TITLE,
          title
        )
        navController.navigateUp()
      }
    )
  }
}

@Composable
private fun Header(onClickBack: () -> Unit) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Icon(
      painter = painterResource(id = R.drawable.ic_back_48),
      contentDescription = null,
      modifier = Modifier.clickable { onClickBack() },
      tint = Color.White
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitleTextField(title: String, onTitleChange: (String) -> Unit) {
  val maxLength = remember { 16 }
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    TextField(
      value = title.trim(),
      onValueChange = {
        if (it.length <= maxLength) {
          onTitleChange(it)
        }
      },
      modifier = Modifier.widthIn(min = 200.dp),
      textStyle = TextStyle(
        color = Color.White,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      ),
      placeholder = {
        Text(
          text = stringResource(id = R.string.timer_editor_title_hint),
          modifier = Modifier.widthIn(min = 200.dp),
          color = Color.White.copy(alpha = 0.5f),
          fontSize = 30.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center
        )
      },
      trailingIcon = {
        Icon(
          imageVector = Icons.Rounded.Cancel,
          contentDescription = null,
          modifier = Modifier.clickable { onTitleChange("") },
          tint = Color.White
        )
      },
      colors = TextFieldDefaults.textFieldColors(
        textColor = Color.White,
        containerColor = Color.Transparent,
        cursorColor = Color.White,
        focusedIndicatorColor = Color.White,
        unfocusedIndicatorColor = Color.White
      )
    )
    Spacer(modifier = Modifier.size(24.dp))
    Text(
      text = "%d/%d".format(title.trim().length, maxLength),
      color = Color.White,
      fontSize = 16.sp
    )
  }
}