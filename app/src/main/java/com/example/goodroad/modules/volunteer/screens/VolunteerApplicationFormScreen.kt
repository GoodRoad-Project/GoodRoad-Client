package com.example.goodroad.ui.volunteer.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

    var isSubmitted by rememberSaveable { mutableStateOf(false) }

    val isLoading by viewModel.isLoading
    val error by viewModel.errorMessage
    val success by viewModel.successMessage

    val scrollState = rememberScrollState()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { selectedUris.add(it) }
    }

    if (isSubmitted) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Заявка отправлена",
                    style = MaterialTheme.typography.headlineLarge,
                    color = UrbanBrown
                )

                Spacer(Modifier.height(12.dp))

                Text("Мы рассмотрим её и свяжемся с вами")

                Spacer(Modifier.height(24.dp))

                PrimaryButton(
                    text = "Понятно",
                    onClick = onSubmitted
                )
            }
        }

    } else {

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
                    "Заявка на волонтёрство",
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(Modifier.height(16.dp))

                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
                if (success != null) Text(success!!, color = MaterialTheme.colorScheme.primary)

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = dobroUrl,
                    onValueChange = {
                        dobroUrl = it
                        viewModel.clearMessages()
                    },
                    label = { Text("Dobro.ru URL") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        viewModel.clearMessages()
                    },
                    label = { Text("Телефон") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = socialNickname,
                    onValueChange = {
                        socialNickname = it
                        viewModel.clearMessages()
                    },
                    label = { Text("Telegram / VK ник") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Button(onClick = { picker.launch("image/*") }) {
                    Text("Добавить сертификаты")
                }

                Spacer(Modifier.height(12.dp))

                if (selectedUris.isNotEmpty()) {

                    Text("Сертификаты:", style = MaterialTheme.typography.titleMedium)

                    Spacer(Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedUris) { uri ->

                            Box {

                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(90.dp)
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
                    text = if (isLoading) "Отправка..." else "Отправить",
                    onClick = {

                        if (dobroUrl.isBlank() || phone.isBlank()) return@PrimaryButton

                        viewModel.submitVolunteerApplication(
                            context = context,
                            dobroUrl = dobroUrl,
                            phone = phone,
                            socialNickname = socialNickname.ifBlank { null },
                            uris = selectedUris,
                            onSuccess = {
                                isSubmitted = true
                            }
                        )
                    }
                )
            }
        }
    }
}