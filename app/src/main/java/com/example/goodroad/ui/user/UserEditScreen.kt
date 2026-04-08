package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.auth.PlainField
import com.example.goodroad.ui.theme.GrayButton
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.WhiteSoft
import com.example.goodroad.ui.viewmodel.UserViewModel

@Composable
fun UserEditScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    val user = userViewModel.user.value ?: return

    var firstName by remember { mutableStateOf(user.firstName ?: "") }
    var lastName by remember { mutableStateOf(user.lastName ?: "") }
    var photoUrl by remember { mutableStateOf(user.photoUrl ?: "") }
    var phone by remember { mutableStateOf(user.phone ?: "") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    val errorMessage by remember { derivedStateOf { userViewModel.errorMessage.value } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        UserDecor()

        Text(
            "Редактирование профиля",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )

        Spacer(Modifier.height(12.dp))
        PlainField(value = firstName, onValueChange = { firstName = it }, label = "Имя")
        Spacer(Modifier.height(12.dp))
        PlainField(value = lastName, onValueChange = { lastName = it }, label = "Фамилия")
        Spacer(Modifier.height(12.dp))
        PlainField(value = photoUrl, onValueChange = { photoUrl = it }, label = "URL фото")
        Spacer(Modifier.height(12.dp))
        PlainField(value = phone, onValueChange = { phone = it }, label = "Телефон")
        Spacer(Modifier.height(12.dp))
        PlainField(value = oldPassword, onValueChange = { oldPassword = it }, label = "Старый пароль")
        Spacer(Modifier.height(12.dp))
        PlainField(value = newPassword, onValueChange = { newPassword = it }, label = "Новый пароль")

        Spacer(Modifier.height(20.dp))
        AuthButton(text = "Сохранить") {
            val oldPass = oldPassword.takeIf { it.isNotBlank() }
            val newPass = newPassword.takeIf { it.isNotBlank() }

            userViewModel.updateUser(
                firstName = firstName,
                lastName = lastName,
                photoUrl = photoUrl.ifBlank { null },
                phone = phone.ifBlank { null },
                oldPassword = oldPass,
                newPassword = newPass
            )

            oldPassword = ""
            newPassword = ""
        }

        Spacer(Modifier.height(12.dp))
        AuthButton(
            text = "Выйти",
            backgroundColor = GrayButton,
            contentColor = WhiteSoft
        ) {
            userViewModel.logout {
                onBack()
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}