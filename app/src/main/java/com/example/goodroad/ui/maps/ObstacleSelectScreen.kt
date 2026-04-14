package com.example.goodroad.ui.maps

import androidx.compose.foundation.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.users.UserDecor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.goodroad.ui.theme.*
data class Obstacle(
    val id: String,
    val name: String
)

@Composable
fun ObstacleSelectScreen(
    onBackToProfile: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val obstacles = remember {
        listOf(
            Obstacle("1", "Ямы да ухабы"),
            Obstacle("2", "Лежачие полицейские...спят, устали"),
            Obstacle("3", "Ремонт дороги...пока что можно только летать"),
            Obstacle("4", "Скользкая дорога...пол помыли"),
            Obstacle("5", "Перекрытие дороги...фильм снимают"),
            Obstacle("6", "Авария...дискотека"),
            Obstacle("7", "Пробка...от вина")
        )
    }

    var selected by remember { mutableStateOf(setOf<String>()) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {

                UserDecor()

                Text(
                    text = "Выбор препятствий",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Выберите препятствия, которые хотите избегать:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = UrbanBrown
                )

                Spacer(modifier = Modifier.height(20.dp))

                obstacles.forEach { obstacle ->
                    val checked = obstacle.id in selected

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                selected = if (isChecked) {
                                    selected + obstacle.id
                                } else {
                                    selected - obstacle.id
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = UrbanBrown,
                                uncheckedColor = UrbanBrown,
                                checkmarkColor = WhiteSoft
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = obstacle.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = UrbanBrown
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                AuthButton(
                    text = "Сохранить"
                ) {
                    onSave(selected.toList())
                }

                Spacer(modifier = Modifier.height(10.dp))

                AuthButton(
                    text = "Назад в профиль",
                    backgroundColor = UrbanBrown,
                    contentColor = WhiteSoft
                ) {
                    onBackToProfile()
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .align(Alignment.CenterEnd)
                    .background(UrbanBrown.copy(alpha = 0.25f))
            )

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(60.dp)
                    .offset(y = (scrollState.value * 0.2f).dp)
                    .align(Alignment.TopEnd)
                    .background(UrbanBrown)
            )
        }
    }
}