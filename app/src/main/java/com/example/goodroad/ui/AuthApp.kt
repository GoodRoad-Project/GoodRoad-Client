package com.example.goodroad.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.goodroad.BuildConfig
import com.example.goodroad.data.network.ApiClient
import com.example.goodroad.data.network.LoginReq
import com.example.goodroad.data.network.RecoverPasswordReq
import com.example.goodroad.data.network.RegisterReq
import com.example.goodroad.ui.theme.AlertRed
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.BorderWarm
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.SafeRoute
import com.example.goodroad.ui.theme.SurfaceWarm
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.TextSecondary
import com.example.goodroad.ui.theme.UrbanBrown
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private const val LOGIN_ROUTE = "login"
private const val REGISTER_ROUTE = "register"
private const val RECOVER_ROUTE = "recover"
private const val USER_HOME_ROUTE = "user_home"
private const val MODERATOR_HOME_ROUTE = "moderator_home"

private val cyrillicInputRegex = Regex("^(?:(?=.*\\p{IsCyrillic})[\\p{IsCyrillic} -]*|[ -]*)$")
private val digitsInputRegex = Regex("^\\d*$")
private val cyrillicValueRegex = Regex("^(?=.*\\p{IsCyrillic})[\\p{IsCyrillic} -]+$")
private val digitsValueRegex = Regex("^\\d+$")

private fun homeRoute(role: String): String {
    return if (role.startsWith("MODERATOR")) {
        MODERATOR_HOME_ROUTE
    } else {
        USER_HOME_ROUTE
    }
}

private fun isAllowedCyrillicInput(value: String): Boolean {
    return cyrillicInputRegex.matches(value)
}

private fun isAllowedDigitsInput(value: String): Boolean {
    return digitsInputRegex.matches(value)
}

private fun normalizeRequiredCyrillic(value: String): String? {
    val normalized = value.trim()
    if (normalized.isEmpty()) {
        return null
    }
    return if (cyrillicValueRegex.matches(normalized)) normalized else null
}

private fun normalizeRequiredDigits(value: String): String? {
    val normalized = value.trim()
    if (normalized.isEmpty()) {
        return null
    }
    return if (digitsValueRegex.matches(normalized)) normalized else null
}

private fun formatPhoneForRequest(phoneDigits: String): String {
    return "+$phoneDigits"
}

@Composable
fun AuthApp(navController: NavHostController = rememberNavController()) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        NavHost(
            navController = navController,
            startDestination = LOGIN_ROUTE
        ) {
            composable(LOGIN_ROUTE) {
                LoginScreen(
                    onLoginSuccess = { role ->
                        navController.navigate(homeRoute(role)) {
                            popUpTo(LOGIN_ROUTE) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onSignUp = {
                        navController.navigate(REGISTER_ROUTE)
                    },
                    onForgotPassword = {
                        navController.navigate(RECOVER_ROUTE)
                    }
                )
            }
            composable(REGISTER_ROUTE) {
                RegisterScreen(
                    onRegisterSuccess = { role ->
                        navController.navigate(homeRoute(role)) {
                            popUpTo(LOGIN_ROUTE) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable(RECOVER_ROUTE) {
                RecoverPasswordScreen(
                    onLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable(USER_HOME_ROUTE) {
                RoleStubScreen(
                    title = "Главный экран пользователя",
                    onLogout = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(USER_HOME_ROUTE) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(MODERATOR_HOME_ROUTE) {
                RoleStubScreen(
                    title = "Главный экран модератора",
                    onLogout = {
                        navController.navigate(LOGIN_ROUTE) {
                            popUpTo(MODERATOR_HOME_ROUTE) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AuthScreenFrame(
        title = "Вход",
        action = {
            AuthButton(
                text = if (loading) "Входим..." else "Войти",
                enabled = !loading
            ) {
                val phoneDigits = normalizeRequiredDigits(phone)
                if (phoneDigits == null || password.isBlank()) {
                    errorText = "Заполните телефон и пароль"
                    return@AuthButton
                }

                if (BuildConfig.MOCK_AUTH) {
                    errorText = null
                    onLoginSuccess("USER")
                    return@AuthButton
                }

                scope.launch {
                    loading = true
                    errorText = null
                    try {
                        val resp = ApiClient.authApi.login(
                            LoginReq(
                                phone = formatPhoneForRequest(phoneDigits),
                                password = password
                            )
                        )
                        val role = resp.user?.role
                        if (role.isNullOrBlank()) {
                            errorText = "Не удалось определить роль пользователя"
                        } else {
                            onLoginSuccess(role)
                        }
                    } catch (_: HttpException) {
                        errorText = "Неверный телефон или пароль"
                    } catch (_: IOException) {
                        errorText = "Нет соединения с сервером"
                    } catch (_: Exception) {
                        errorText = "Ошибка входа"
                    } finally {
                        loading = false
                    }
                }
            }
        },
        footer = {
            AuthFooter(
                prefix = "Нет аккаунта?",
                action = "Зарегистрироваться",
                onClick = onSignUp
            )
        }
    ) {
        PhoneField(
            value = phone,
            onValueChange = { value ->
                if (isAllowedDigitsInput(value)) {
                    phone = value
                }
            },
            label = "Телефон"
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль"
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onForgotPassword,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Забыли пароль?",
                    style = MaterialTheme.typography.bodySmall,
                    color = UrbanBrown
                )
            }
        }
        AuthStatusText(text = errorText)
    }
}

@Composable
private fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onLogin: () -> Unit
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AuthScreenFrame(
        title = "Создать аккаунт",
        action = {
            AuthButton(
                text = if (loading) "Создаем..." else "Зарегистрироваться",
                enabled = !loading
            ) {
                val firstNameNormalized = normalizeRequiredCyrillic(firstName)
                if (firstNameNormalized == null) {
                    errorText = "Имя обязательно: только кириллица, пробел и -"
                    return@AuthButton
                }

                val lastNameNormalized = normalizeRequiredCyrillic(lastName)
                if (lastNameNormalized == null) {
                    errorText = "Фамилия обязательна: только кириллица, пробел и -"
                    return@AuthButton
                }

                val phoneDigits = normalizeRequiredDigits(phone)
                if (phoneDigits == null || password.isBlank()) {
                    errorText = "Телефон и пароль обязательны"
                    return@AuthButton
                }
                if (password != confirmPassword) {
                    errorText = "Пароли не совпадают"
                    return@AuthButton
                }

                if (BuildConfig.MOCK_AUTH) {
                    errorText = null
                    onRegisterSuccess("USER")
                    return@AuthButton
                }

                scope.launch {
                    loading = true
                    errorText = null
                    try {
                        val resp = ApiClient.authApi.register(
                            RegisterReq(
                                firstName = firstNameNormalized,
                                lastName = lastNameNormalized,
                                phone = formatPhoneForRequest(phoneDigits),
                                password = password
                            )
                        )
                        val role = resp.user?.role ?: "USER"
                        onRegisterSuccess(role)
                    } catch (_: HttpException) {
                        errorText = "Не удалось зарегистрироваться"
                    } catch (_: IOException) {
                        errorText = "Нет соединения с сервером"
                    } catch (_: Exception) {
                        errorText = "Ошибка регистрации"
                    } finally {
                        loading = false
                    }
                }
            }
        },
        footer = {
            AuthFooter(
                prefix = "Уже есть аккаунт?",
                action = "Войти",
                onClick = onLogin
            )
        }
    ) {
        PlainField(
            value = firstName,
            onValueChange = { value ->
                if (isAllowedCyrillicInput(value)) {
                    firstName = value
                }
            },
            label = "Имя",
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = UrbanBrown
                )
            }
        )
        Spacer(Modifier.height(12.dp))
        PlainField(
            value = lastName,
            onValueChange = { value ->
                if (isAllowedCyrillicInput(value)) {
                    lastName = value
                }
            },
            label = "Фамилия",
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = UrbanBrown
                )
            }
        )
        Spacer(Modifier.height(12.dp))
        PhoneField(
            value = phone,
            onValueChange = { value ->
                if (isAllowedDigitsInput(value)) {
                    phone = value
                }
            },
            label = "Телефон"
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль"
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Подтвердите пароль"
        )
        AuthStatusText(text = errorText)
    }
}

@Composable
private fun RecoverPasswordScreen(
    onLogin: () -> Unit
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var errorText by rememberSaveable { mutableStateOf<String?>(null) }
    var successText by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AuthScreenFrame(
        title = "Смена пароля",
        subtitle = "Для восстановления введите имя, фамилию, номер телефона и новый пароль.",
        action = {
            AuthButton(
                text = if (loading) "Сохраняем..." else "Сменить пароль",
                enabled = !loading
            ) {
                val firstNameNormalized = normalizeRequiredCyrillic(firstName)
                if (firstNameNormalized == null) {
                    errorText = "Имя обязательно: только кириллица, пробел и -"
                    successText = null
                    return@AuthButton
                }

                val lastNameNormalized = normalizeRequiredCyrillic(lastName)
                if (lastNameNormalized == null) {
                    errorText = "Фамилия обязательна: только кириллица, пробел и -"
                    successText = null
                    return@AuthButton
                }

                val phoneDigits = normalizeRequiredDigits(phone)
                if (phoneDigits == null || newPassword.isBlank() || confirmPassword.isBlank()) {
                    errorText = "Заполните все поля"
                    successText = null
                    return@AuthButton
                }
                if (newPassword != confirmPassword) {
                    errorText = "Пароли не совпадают"
                    successText = null
                    return@AuthButton
                }

                if (BuildConfig.MOCK_AUTH) {
                    errorText = null
                    successText = "Пароль успешно изменен. Теперь можно войти."
                    firstName = ""
                    lastName = ""
                    phone = ""
                    newPassword = ""
                    confirmPassword = ""
                    return@AuthButton
                }

                scope.launch {
                    loading = true
                    errorText = null
                    successText = null
                    try {
                        ApiClient.authApi.recoverPassword(
                            RecoverPasswordReq(
                                phone = formatPhoneForRequest(phoneDigits),
                                firstName = firstNameNormalized,
                                lastName = lastNameNormalized,
                                newPassword = newPassword
                            )
                        )
                        successText = "Пароль успешно изменен. Теперь можно войти."
                        firstName = ""
                        lastName = ""
                        phone = ""
                        newPassword = ""
                        confirmPassword = ""
                    } catch (_: HttpException) {
                        errorText = "Не удалось восстановить пароль"
                    } catch (_: IOException) {
                        errorText = "Нет соединения с сервером"
                    } catch (_: Exception) {
                        errorText = "Ошибка смены пароля"
                    } finally {
                        loading = false
                    }
                }
            }
        },
        footer = {
            AuthFooter(
                prefix = "Вспомнили пароль?",
                action = "Вернуться ко входу",
                onClick = onLogin
            )
        }
    ) {
        PlainField(
            value = firstName,
            onValueChange = { value ->
                if (isAllowedCyrillicInput(value)) {
                    firstName = value
                }
            },
            label = "Имя",
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = UrbanBrown
                )
            }
        )
        Spacer(Modifier.height(12.dp))
        PlainField(
            value = lastName,
            onValueChange = { value ->
                if (isAllowedCyrillicInput(value)) {
                    lastName = value
                }
            },
            label = "Фамилия",
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = UrbanBrown
                )
            }
        )
        Spacer(Modifier.height(12.dp))
        PhoneField(
            value = phone,
            onValueChange = { value ->
                if (isAllowedDigitsInput(value)) {
                    phone = value
                }
            },
            label = "Телефон"
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = "Новый пароль"
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Подтвердите пароль"
        )
        AuthStatusText(text = errorText)
        AuthSuccessText(text = successText)
    }
}

@Composable
private fun AuthScreenFrame(
    title: String,
    subtitle: String? = null,
    action: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        AuthDecor()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.height(28.dp))
            content()
            Spacer(Modifier.height(28.dp))
            action()
            Spacer(Modifier.height(16.dp))
            footer()
        }
    }
}

@Composable
private fun AuthDecor() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(SurfaceWarm)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val lightPatch = Path().apply {
                moveTo(0f, size.height * 0.76f)
                cubicTo(
                    size.width * 0.1f, size.height * 0.7f,
                    size.width * 0.2f, size.height * 0.45f,
                    size.width * 0.34f, size.height * 0.42f
                )
                cubicTo(
                    size.width * 0.49f, size.height * 0.38f,
                    size.width * 0.54f, size.height * 0.18f,
                    size.width * 0.72f, size.height * 0.12f
                )
                lineTo(size.width, size.height * 0.12f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path = lightPatch, color = BackgroundLight)

            val road = Path().apply {
                moveTo(size.width * 0.84f, -12f)
                cubicTo(
                    size.width * 0.72f, size.height * 0.1f,
                    size.width * 0.58f, size.height * 0.24f,
                    size.width * 0.56f, size.height * 0.42f
                )
                cubicTo(
                    size.width * 0.54f, size.height * 0.55f,
                    size.width * 0.42f, size.height * 0.67f,
                    size.width * 0.22f, size.height * 0.71f
                )
                cubicTo(
                    size.width * 0.08f, size.height * 0.75f,
                    size.width * 0.02f, size.height * 0.87f,
                    -16f, size.height
                )
            }
            drawPath(
                path = road,
                brush = SolidColor(UrbanBrown),
                style = Stroke(
                    width = size.width * 0.12f,
                    cap = StrokeCap.Round
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(BackgroundLight.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GR",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    PlainField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        icon = {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = UrbanBrown
            )
        },
        prefix = {
            Text(
                text = "+",
                color = TextSecondary
            )
        }
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    var visible by remember { mutableStateOf(false) }

    PlainField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = UrbanBrown
            )
        },
        trailing = {
            Icon(
                imageVector = if (visible) {
                    Icons.Default.VisibilityOff
                } else {
                    Icons.Default.Visibility
                },
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.clickable { visible = !visible }
            )
        }
    )
}

@Composable
private fun PlainField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    icon: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label,
                color = UrbanBrown
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = icon,
        trailingIcon = trailing,
        prefix = prefix,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BackgroundLight,
            unfocusedContainerColor = BackgroundLight,
            disabledContainerColor = BackgroundLight,
            focusedIndicatorColor = SafeGreen,
            unfocusedIndicatorColor = BorderWarm,
            cursorColor = SafeGreen,
            focusedLabelColor = UrbanBrown,
            unfocusedLabelColor = UrbanBrown,
            focusedLeadingIconColor = UrbanBrown,
            unfocusedLeadingIconColor = UrbanBrown,
            focusedTrailingIconColor = TextSecondary,
            unfocusedTrailingIconColor = TextSecondary
        ),
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun AuthButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SafeRoute,
            contentColor = BackgroundLight,
            disabledContainerColor = SafeRoute.copy(alpha = 0.6f),
            disabledContentColor = BackgroundLight
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AuthFooter(
    prefix: String,
    action: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prefix,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(Modifier.width(4.dp))
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = action,
                style = MaterialTheme.typography.bodyMedium,
                color = UrbanBrown,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AuthStatusText(
    text: String?
) {
    if (text == null) {
        return
    }
    Spacer(Modifier.height(12.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = AlertRed
    )
}

@Composable
private fun AuthSuccessText(
    text: String?
) {
    if (text == null) {
        return
    }
    Spacer(Modifier.height(12.dp))
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = SafeGreen
    )
}

@Composable
private fun RoleStubScreen(
    title: String,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Spacer(Modifier.height(20.dp))
            AuthButton(
                text = "Выйти",
                onClick = onLogout
            )
        }
    }
}