package com.example.goodroad.modules.moderator.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.goodroad.modules.moderator.presentation.VolunteerModerationViewModel
import com.example.goodroad.modules.moderator.data.VolunteerApplicationResp
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.*

@Composable
fun VolunteerManagementScreen(
    viewModel: VolunteerModerationViewModel,
    onBack: () -> Unit
) {
    val apps by viewModel.applications.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var rejectId by remember { mutableStateOf<String?>(null) }
    var rejectReason by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            UserDecor()

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Заявки волонтёров",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(16.dp))

            when {
                loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = UrbanBrown)
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Ошибка",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    if (apps.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = SafeGreen
                                )

                                Spacer(Modifier.height(16.dp))

                                Text(
                                    text = "Нет заявок на модерации",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = UrbanBrown
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(apps, key = { it.id }) { app ->
                                VolunteerApplicationCard(
                                    app = app,
                                    onApprove = { viewModel.approve(app.id) },
                                    onReject = {
                                        rejectId = app.id
                                        rejectReason = TextFieldValue("")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            PrimaryButton(
                text = "Назад в профиль",
                backgroundColor = UrbanBrown,
                contentColor = UrbanBrown,
                onClick = onBack
            )
        }
    }

    if (rejectId != null) {
        AlertDialog(
            onDismissRequest = { rejectId = null },
            title = { Text("Причина отказа") },
            text = {
                OutlinedTextField(
                    value = rejectReason,
                    onValueChange = { rejectReason = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Введите причину отказа") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reject(rejectId!!, rejectReason.text)
                        rejectId = null
                    }
                ) {
                    Text("Отправить")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectId = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun VolunteerApplicationCard(
    app: VolunteerApplicationResp,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWarm),
        border = BorderStroke(1.dp, BorderWarm)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = app.applicantName,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                Text(
                    text = "Телефон: ${app.phone}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Социальный профиль: ${app.socialNickname ?: "не указан"}",
                    style = MaterialTheme.typography.bodyLarge
                )

                if (!app.dobroUrl.isNullOrBlank()) {
                    Text(
                        text = "Открыть профиль Dobro.ru",
                        style = MaterialTheme.typography.titleMedium,
                        color = InclusiveViolet,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.dobroUrl))
                            context.startActivity(intent)
                        }
                    )
                } else {
                    Text(
                        text = "Dobro.ru: —",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrimaryButton(
                    text = "Одобрить",
                    backgroundColor = SafeGreen,
                    onClick = onApprove,
                    modifier = Modifier.weight(1f)
                )

                PrimaryButton(
                    text = "Отклонить",
                    backgroundColor = AlertRed,
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}