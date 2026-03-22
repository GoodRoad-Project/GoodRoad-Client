package com.example.goodroad.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodroad.ui.auth.AuthButton
import com.example.goodroad.ui.auth.PlainField
import com.example.goodroad.ui.theme.TextPrimary

@Composable
fun UserEditScreen(
    vm: UserViewModel,
    onBack: () -> Unit
) {
    val user = vm.user.value ?: return

    var firstName by remember { mutableStateOf(user.firstName ?: "") }
    var lastName by remember { mutableStateOf(user.lastName ?: "") }
    var photoUrl by remember { mutableStateOf(user.photoUrl ?: "") }

    Column(modifier = Modifier.padding(24.dp)) {
        UserDecor()

        Text(
            "Редактирование профиля",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )

        PlainField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "Имя"
        )

        Spacer(Modifier.height(12.dp))

        PlainField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Фамилия"
        )

        PlainField(
            value = photoUrl,
            onValueChange = { photoUrl = it },
            label = "URL фото, телефон и пароль(на сервере функцию пофиксить): TODO"
        )



        Spacer(Modifier.height(20.dp))

        AuthButton(
            text = "Сохранить"
        ) {
            vm.update(firstName, lastName)
            onBack()
        }
    }
}