package com.example.goodroad.ui.volunteer.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.goodroad.modules.volunteer.presentation.VolunteerViewModel
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.UrbanBrown

@Composable
fun VolunteerApplicationFormScreen(
    viewModel: VolunteerViewModel,
    onBack: () -> Unit,
    onSubmitted: () -> Unit
) {
    val context = LocalContext.current

    var dobroUrl by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var socialNickname by rememberSaveable { mutableStateOf("") }

    val selectedUris = remember { mutableStateListOf<Uri>() }
    val scrollState = rememberScrollState()

    val isLoading by viewModel.isLoading
    val error by viewModel.errorMessage
    val success by viewModel.successMessage

    LaunchedEffect(Unit) {
        viewModel.clearMessages()
        viewModel.loadVolunteerMenu()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearMessages()
        }
    }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { selectedUris.add(it) }
    }

    val menu = viewModel.volunteerMenu.value
    val applicationStatus = menu?.applicationStatus
    val rejectReason = menu?.rejectReason

    if (menu != null && applicationStatus != null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundLight
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    UserDecor()

                    Text(
                        text = when (applicationStatus) {
                            "PENDING" -> "Ваша заявка на рассмотрении"
                            "APPROVED" -> "Вы уже волонтёр!"
                            "REJECTED" -> "Заявка отклонена"
                            else -> "Статус заявки"
                        },
                        style = MaterialTheme.typography.headlineLarge,
                        color = UrbanBrown
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = when (applicationStatus) {
                            "PENDING" -> "Мы рассмотрим её и свяжемся с вами в течение недели!"
                            "APPROVED" -> "У вас уже есть доступ к списку заявок на помощь"
                            "REJECTED" -> "Причина отказа: ${rejectReason ?: "не указана"}"
                            else -> "Текущий статус: $applicationStatus"
                        },
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.weight(1f))

                    if (applicationStatus == "REJECTED") {
                        PrimaryButton(
                            text = "Подать заявку заново",
                            onClick = {
                                viewModel.volunteerMenu.value = null
                                viewModel.clearMessages()
                            }
                        )
                    } else {
                        PrimaryButton(
                            text = "Понятно",
                            onClick = onSubmitted
                        )
                    }
                }
            }
        }
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            UserDecor()

            Text(
                text = "Заявка на волонтёрство",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(16.dp))

            if (error != null) {
                ErrorBlock(mapErrorToUserMessage(error))
            }

            if (success != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = success!!,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = dobroUrl,
                onValueChange = {
                    dobroUrl = it
                    viewModel.clearMessages()
                },
                label = { Text("Dobro.ru URL *") },
                modifier = Modifier.fillMaxWidth(),
                isError = error?.contains("dobro", ignoreCase = true) == true,
                supportingText = {
                    if (error?.contains("dobro", ignoreCase = true) == true) {
                        Text("Проверьте ссылку")
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    viewModel.clearMessages()
                },
                label = { Text("Телефон *") },
                modifier = Modifier.fillMaxWidth(),
                isError = error?.contains("phone", ignoreCase = true) == true,
                supportingText = {
                    if (error?.contains("phone", ignoreCase = true) == true) {
                        Text("11 цифр, например: 79123456789")
                    }
                }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = socialNickname,
                onValueChange = {
                    socialNickname = it
                    viewModel.clearMessages()
                },
                label = { Text("Telegram / VK ник (опционально)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(onClick = { picker.launch("image/*") }) {
                Text("Добавить сертификаты")
            }

            Spacer(Modifier.height(12.dp))

            if (selectedUris.isNotEmpty()) {
                Text("Сертификаты:")
                Spacer(Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedUris) { uri ->
                        Box {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(90.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            TextButton(
                                onClick = { selectedUris.remove(uri) },
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text("✕")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = if (isLoading) "Отправка..." else "Отправить заявку",
                onClick = {
                    viewModel.clearMessages()

                    if (dobroUrl.isBlank()) {
                        viewModel.errorMessage.value = "Укажите ссылку на профиль Dobro.ru"
                        return@PrimaryButton
                    }

                    if (!dobroUrl.startsWith("http")) {
                        viewModel.errorMessage.value = "Ссылка должна начинаться с http:// или https://"
                        return@PrimaryButton
                    }

                    if (phone.isBlank()) {
                        viewModel.errorMessage.value = "Укажите номер телефона"
                        return@PrimaryButton
                    }

                    val phoneDigits = phone.filter { it.isDigit() }
                    if (phoneDigits.length != 11) {
                        viewModel.errorMessage.value = "Номер должен содержать ровно 11 цифр"
                        return@PrimaryButton
                    }

                    val nickname = socialNickname.trim().removePrefix("@")
                    if (nickname.isNotBlank()) {
                        val nicknameRegex = Regex("^[a-zA-Z0-9_.]{3,32}$")
                        if (!nicknameRegex.matches(nickname)) {
                            viewModel.errorMessage.value = "Ник должен содержать только латиницу, цифры, _ или . (3-32 символа)"
                            return@PrimaryButton
                        }
                    }

                    viewModel.submitVolunteerApplication(
                        context = context,
                        dobroUrl = dobroUrl.trim(),
                        phone = phoneDigits,
                        socialNickname = nickname.ifBlank { null },
                        uris = selectedUris,
                        onSuccess = {
                            viewModel.loadVolunteerMenu()
                            onSubmitted()
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun ErrorBlock(error: String?) {
    if (error.isNullOrBlank()) return

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(12.dp)
        )
    }

    Spacer(Modifier.height(8.dp))
}

private fun mapErrorToUserMessage(error: String?): String {
    val msg = error?.lowercase() ?: return "Произошла неизвестная ошибка"

    return when {
        msg.contains("timeout") || msg.contains("timed out") -> "Сервер не отвечает. Проверьте интернет и попробуйте позже"
        msg.contains("unable to resolve host") -> "Нет соединения с сервером. Проверьте интернет"
        msg.contains("403") || msg.contains("forbidden") -> "Доступ запрещён. Выйдите из приложения и войдите заново"
        msg.contains("400") -> "Проверьте правильность заполнения всех полей"
        msg.contains("401") || msg.contains("unauthorized") -> "Сессия истекла. Войдите в приложение заново"
        msg.contains("404") -> "Сервис временно недоступен. Попробуйте позже"
        msg.contains("409") -> "Заявка уже существует или произошёл конфликт"
        msg.contains("422") -> "Проверьте правильность введённых данных"
        msg.contains("500") || msg.contains("502") || msg.contains("503") -> "Ошибка на сервере. Попробуйте позже"
        msg.contains("validation") -> "Некоторые поля заполнены неверно"
        msg.contains("url") || msg.contains("dobro") -> "Проверьте правильность ссылки на Dobro.ru"
        msg.contains("phone") -> "Проверьте правильность номера телефона"
        msg.contains("nickname") -> "Проверьте правильность Telegram/VK ника"
        msg.contains("already") && msg.contains("volunteer") -> "Вы уже являетесь волонтёром"
        msg.contains("already") && msg.contains("pending") -> "У вас уже есть заявка на рассмотрении"
        else -> error
    }
}