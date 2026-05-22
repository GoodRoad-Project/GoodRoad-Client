package com.example.goodroad.modules.help.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.help.presentation.HelpViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.*
import androidx.compose.ui.text.font.FontWeight

@Composable
fun UserRequestsScreen(
    helpViewModel: HelpViewModel
) {

    val requests = helpViewModel.requests

    var deleteId by remember { mutableStateOf<String?>(null) }

    fun statusText(status: HelpViewModel.RequestStatus): String {
        return when (status) {
            HelpViewModel.RequestStatus.PENDING -> "В обработке"
            HelpViewModel.RequestStatus.APPROVED -> "Одобрено"
            HelpViewModel.RequestStatus.REJECTED -> "Отклонено"
            HelpViewModel.RequestStatus.COMPLETED -> "Выполнено"
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            UserDecor()

            Text(
                "Мои заявки",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {

                items(requests, key = { it.id }) { req ->

                    OutlinedCard(
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            UrbanBrown.copy(alpha = 0.4f)
                        ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = BackgroundLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(Modifier.padding(16.dp)) {

                            Text(
                                "Маршрут",
                                color = UrbanBrown,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "${req.routeStart}  ➜  ${req.routeEnd}",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Дата",
                                color = UrbanBrown,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(req.dateTime)

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Контакт",
                                color = UrbanBrown,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(req.contact)

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Особенности",
                                color = UrbanBrown,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(req.specialNotes)

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Комментарий",
                                color = UrbanBrown,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(req.comment)

                            Spacer(Modifier.height(10.dp))

                            Text(
                                "Статус",
                                color = UrbanBrown,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(statusText(req.status))

                            Spacer(Modifier.height(12.dp))

                            PrimaryButton(
                                text = "Удалить",
                                onClick = { deleteId = req.id }
                            )
                        }
                    }
                }
            }
        }
    }

    if (deleteId != null) {

        AlertDialog(
            onDismissRequest = { deleteId = null },
            title = { Text("Удаление") },
            text = { Text("Удалить заявку?") },
            confirmButton = {
                TextButton(onClick = {
                    helpViewModel.deleteRequest(deleteId!!)
                    deleteId = null
                }) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteId = null }) {
                    Text("Нет")
                }
            }
        )
    }
}