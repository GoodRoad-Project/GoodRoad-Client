package com.example.goodroad.modules.review.screens

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goodroad.modules.review.data.ReviewAddress
import com.example.goodroad.modules.review.data.ReviewCardResp
import com.example.goodroad.modules.review.data.ReviewObstacle
import com.example.goodroad.modules.review.data.UpsertReviewReq
import com.example.goodroad.modules.review.presentation.ReviewsViewModel
import com.example.goodroad.ui.AuthStatusText
import com.example.goodroad.ui.ReviewObstacleTypes
import com.example.goodroad.ui.ReviewPhotosStrip
import com.example.goodroad.ui.SeveritySelector
import com.example.goodroad.ui.UserDecor
import com.example.goodroad.ui.buttons.PrimaryButton
import com.example.goodroad.ui.fields.PlainField
import com.example.goodroad.ui.obstacleLabel
import com.example.goodroad.ui.theme.BackgroundLight
import com.example.goodroad.ui.theme.BorderWarm
import com.example.goodroad.ui.theme.SafeGreen
import com.example.goodroad.ui.theme.TextPrimary
import com.example.goodroad.ui.theme.UrbanBrown
import com.example.goodroad.ui.theme.WhiteSoft
import com.example.goodroad.validation.COMMENT_MAX_LENGTH
import com.example.goodroad.validation.COORDINATE_MAX_LENGTH
import com.example.goodroad.validation.PLACE_NAME_MAX_LENGTH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun ReviewFormScreen(
    reviewsViewModel: ReviewsViewModel,
    initialReview: ReviewCardResp?,
    initialPlaceName: String = "",
    initialLatitude: String = "",
    initialLongitude: String = "",
    isLocationLocked: Boolean = false,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    taskTargetId: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isEdit = initialReview != null
    val reviewKey = initialReview?.id ?: "new"

    var rating by remember(reviewKey) {
        mutableStateOf(initialReview?.rating?.toInt())
    }

    var comment by remember(reviewKey) {
        mutableStateOf(initialReview?.comment ?: "")
    }

    var formError by remember(reviewKey) {
        mutableStateOf<String?>(null)
    }

    var placeName by remember(reviewKey) {
        mutableStateOf(initialReview?.address?.placeName ?: initialPlaceName)
    }

    var latitude by remember(reviewKey) {
        mutableStateOf(initialReview?.latitude?.toString() ?: initialLatitude)
    }

    var longitude by remember(reviewKey) {
        mutableStateOf(initialReview?.longitude?.toString() ?: initialLongitude)
    }

    val photoUrls = remember(reviewKey) {
        mutableStateListOf<String>().apply {
            addAll(initialReview?.photoUrls.orEmpty())
        }
    }

    val obstacleSelected = remember(reviewKey) {
        mutableStateMapOf<String, Boolean>().apply {
            ReviewObstacleTypes.forEach { type ->
                val initialSeverity = initialReview?.obstacles
                    ?.firstOrNull { it.obstacleType == type }
                    ?.severity
                    ?.toInt()
                    ?: 0

                put(type, initialSeverity > 0)
            }
        }
    }

    val obstacleSeverities = remember(reviewKey) {
        mutableStateMapOf<String, Int>().apply {
            ReviewObstacleTypes.forEach { type ->
                val initialSeverity = initialReview?.obstacles
                    ?.firstOrNull { it.obstacleType == type }
                    ?.severity
                    ?.toInt()
                    ?: 0

                put(
                    type,
                    if (initialSeverity > 0) initialSeverity else 1
                )
            }
        }
    }

    val isSubmitting by reviewsViewModel.isSubmitting
    val isPhotoUploading by reviewsViewModel.isPhotoUploading
    val serverError by reviewsViewModel.errorMessage

    var isPreparingSubmit by remember(reviewKey) {
        mutableStateOf(false)
    }

    val submitInProgress = isSubmitting || isPreparingSubmit

    LaunchedEffect(isSubmitting) {
        if (isSubmitting) {
            isPreparingSubmit = false
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            reviewsViewModel.uploadReviewPhotos(context, uris) { uploadedUrls ->
                uploadedUrls
                    .filter { it.isNotBlank() }
                    .forEach { photoUrls.add(it) }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {

            Text(
                text = if (isEdit) {
                    "Редактирование отзыва"
                } else {
                    "Новый отзыв"
                },
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(12.dp))

            PlainField(
                value = placeName,
                onValueChange = {
                    if (!isLocationLocked) {
                        placeName = it
                    }
                },
                label = "Название места",
                maxLength = PLACE_NAME_MAX_LENGTH,
                readOnly = isLocationLocked
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                PlainField(
                    value = latitude,
                    onValueChange = {
                        if (!isLocationLocked) {
                            latitude = it
                        }
                    },
                    label = "Широта",
                    maxLength = COORDINATE_MAX_LENGTH,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    readOnly = isLocationLocked
                )

                PlainField(
                    value = longitude,
                    onValueChange = {
                        if (!isLocationLocked) {
                            longitude = it
                        }
                    },
                    label = "Долгота",
                    maxLength = COORDINATE_MAX_LENGTH,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    readOnly = isLocationLocked
                )
            }

            Spacer(Modifier.height(12.dp))

            if (isLocationLocked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = UrbanBrown,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(18.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Адрес и координаты нельзя изменить, " +
                                "поскольку отзыв создаётся для конкретной " +
                                "цели задания. Местоположение уже задано " +
                                "этой целью и должно оставаться неизменным.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            } else {
                Text(
                    text = "Адрес будет определен автоматически по введенным координатам.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = UrbanBrown
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Координаты должны быть в числовом формате. " +
                            "Можно использовать точку или запятую.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = UrbanBrown
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Оценка",
                style = MaterialTheme.typography.titleMedium,
                color = UrbanBrown.copy(
                    red = UrbanBrown.red * 0.7f,
                    green = UrbanBrown.green * 0.5f,
                    blue = UrbanBrown.blue * 0.5f
                ),
            )

            Spacer(Modifier.height(8.dp))

            SeveritySelector(
                value = rating,
                range = 1..5,
                onValueChange = { rating = it }
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Препятствия и их тяжесть",
                style = MaterialTheme.typography.titleMedium,
                color = UrbanBrown.copy(
                    red = UrbanBrown.red * 0.7f,
                    green = UrbanBrown.green * 0.5f,
                    blue = UrbanBrown.blue * 0.5f
                ),
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Если чекбокс не выбран, то такого препятствия в этом месте нет.",
                style = MaterialTheme.typography.bodyMedium,
                color = UrbanBrown
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Хотя бы у одного препятствия должна быть выбрана тяжесть.",
                style = MaterialTheme.typography.bodyMedium,
                color = UrbanBrown
            )

            ReviewObstacleTypes.forEach { type ->

                val selected = obstacleSelected[type] == true
                val severity = obstacleSeverities[type] ?: 1

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = selected,
                            onCheckedChange = { isChecked ->
                                obstacleSelected[type] = isChecked

                                if (!isChecked) {
                                    obstacleSeverities[type] = 1
                                }

                                formError = null
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = UrbanBrown,
                                uncheckedColor = UrbanBrown,
                                checkmarkColor = WhiteSoft
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = obstacleLabel(type),
                            style = MaterialTheme.typography.titleMedium,
                            color = UrbanBrown
                        )
                    }

                    if (selected) {

                        Column(
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            val severityDescriptions = when (type) {
                                "STAIRS" -> listOf(
                                    "1-3 ступеньки",
                                    "4-10 ступенек",
                                    "Более 10 ступенек"
                                )
                                "CURB" -> listOf(
                                    "Маленький бордюр",
                                    "Обычный бордюр",
                                    "Высокий бордюр"
                                )
                                "ROAD_SLOPE" -> listOf(
                                    "Незначительный подъём",
                                    "Заметный подъём",
                                    "Крутой подъём"
                                )
                                "POTHOLES" -> listOf(
                                    "Маленькая яма",
                                    "Обычная яма",
                                    "Большая яма"
                                )
                                "SAND", "GRAVEL" -> listOf(
                                    "Укатанный песок",
                                    "Немного рыхлый песок",
                                    "Сильно рыхлый песок"
                                )
                                else -> listOf(
                                    "слабая",
                                    "средняя",
                                    "сильная"
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier.padding(start = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                (0..2).forEach { index ->
                                    val value = index + 1
                                    val description = severityDescriptions.getOrElse(index) { "$value" }
                                    val isSelected = severity == value

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                obstacleSeverities[type] = value
                                                formError = null
                                            }
                                            .background(
                                                color = if (isSelected) SafeGreen.copy(alpha = 0.12f) else BackgroundLight,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) SafeGreen else BorderWarm.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(vertical = 10.dp, horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(
                                                    color = if (isSelected) SafeGreen else UrbanBrown.copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(16.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = value.toString(),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else UrbanBrown
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isSelected) SafeGreen else UrbanBrown,
                                            fontSize = 15.sp,
                                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            TextField(
                value = comment,
                onValueChange = { value ->
                    if (value.length <= COMMENT_MAX_LENGTH) {
                        comment = value
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                label = {
                    Text(
                        text = "Комментарий",
                        color = UrbanBrown
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary
                ),
                singleLine = false,
                minLines = 1,
                maxLines = 5,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BackgroundLight,
                    unfocusedContainerColor = BackgroundLight,
                    focusedIndicatorColor = SafeGreen,
                    unfocusedIndicatorColor = BorderWarm,
                    focusedLabelColor = UrbanBrown,
                    unfocusedLabelColor = UrbanBrown,
                    cursorColor = SafeGreen
                )
            )

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BackgroundLight,
                    contentColor = UrbanBrown
                ),
                border = BorderStroke(1.dp, BorderWarm)
            ) {

                Icon(
                    imageVector = Icons.Filled.Photo,
                    contentDescription = null,
                    tint = UrbanBrown
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = if (isPhotoUploading) {
                        "Загружаем фото..."
                    } else {
                        "Добавить фотографии"
                    },
                    color = UrbanBrown
                )
            }

            Spacer(Modifier.height(12.dp))

            ReviewPhotosStrip(
                photoUrls = photoUrls,
                onRemove = { photoUrls.remove(it) }
            )

            AuthStatusText(
                text = formError ?: serverError,
                onTimeout = {
                    formError = null
                    reviewsViewModel.clearMessages()
                }
            )

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = when {
                    isPhotoUploading -> "Загружаем фото..."
                    submitInProgress -> "Сохраняем..."
                    isEdit -> "Сохранить изменения"
                    else -> "Отправить отзыв"
                },
                enabled = !submitInProgress && !isPhotoUploading
            ) {

                scope.launch {

                    isPreparingSubmit = true
                    var sentToViewModel = false

                    try {

                        val validationError = validateReviewForm(
                            latitude = latitude,
                            longitude = longitude,
                            rating = rating,
                            obstacleSelected = obstacleSelected,
                            obstacleSeverities = obstacleSeverities
                        )

                        if (validationError != null) {
                            formError = validationError
                            return@launch
                        }

                        val lat = latitude
                            .trim()
                            .replace(',', '.')
                            .toDouble()

                        val lon = longitude
                            .trim()
                            .replace(',', '.')
                            .toDouble()

                        val generatedAddress = resolveReviewAddress(
                            context = context,
                            latitude = lat,
                            longitude = lon,
                            placeName = placeName,
                            fallbackAddress = initialReview?.address
                        )

                        formError = null
                        reviewsViewModel.clearMessages()

                        val obstacles = ReviewObstacleTypes.map { type ->

                            ReviewObstacle(
                                obstacleType = type,
                                severity = if (obstacleSelected[type] == true) {
                                    (obstacleSeverities[type] ?: 1).toShort()
                                } else {
                                    0
                                }
                            )
                        }

                        val request = UpsertReviewReq(
                            latitude = lat,
                            longitude = lon,
                            address = generatedAddress,
                            rating = rating!!.toShort(),
                            obstacles = obstacles,
                            comment = comment.trim().ifBlank { null },
                            photoUrls = photoUrls.filter { it.isNotBlank() },
                            taskTargetId = taskTargetId
                        )

                        sentToViewModel = true

                        if (isEdit) {
                            reviewsViewModel.updateReview(
                                initialReview!!.id,
                                request,
                                onSaved
                            )
                        } else {
                            reviewsViewModel.createReview(
                                request,
                                onSaved
                            )
                        }

                    } finally {

                        if (!sentToViewModel) {
                            isPreparingSubmit = false
                        }
                    }
                }
            }
        }
    }
}

private fun validateReviewForm(
    latitude: String,
    longitude: String,
    rating: Int?,
    obstacleSelected: Map<String, Boolean>,
    obstacleSeverities: Map<String, Int>
): String? {

    val lat = latitude
        .trim()
        .replace(',', '.')
        .toDoubleOrNull()

    val lon = longitude
        .trim()
        .replace(',', '.')
        .toDoubleOrNull()

    if (lat == null || lon == null) {
        return "Введите корректные координаты"
    }

    if (lat < -90.0 || lat > 90.0 ||
        lon < -180.0 || lon > 180.0
    ) {
        return "Координаты выходят за допустимый диапазон"
    }

    if (rating == null) {
        return "Поставьте оценку отзыву"
    }

    if (obstacleSelected.values.none { it }) {
        return "Выберите хотя бы одно препятствие"
    }

    return null
}

private suspend fun resolveReviewAddress(
    context: Context,
    latitude: Double,
    longitude: Double,
    placeName: String,
    fallbackAddress: ReviewAddress?
): ReviewAddress = withContext(Dispatchers.IO) {

    val normalizedPlaceName =
        placeName.trim().ifBlank { null }

    val baseAddress = fallbackAddress ?: ReviewAddress(
        country = "Россия",
        region = "Регион не указан",
        localityType = "город",
        city = "Населенный пункт не указан",
        street = "Улица не указана",
        house = "Без номера",
        placeName = normalizedPlaceName
    )

    val geocoder = Geocoder(
        context,
        Locale("ru")
    )

    return@withContext try {

        val rawAddress = geocoder
            .getFromLocation(latitude, longitude, 1)
            ?.firstOrNull()

        if (rawAddress == null) {

            baseAddress.copy(
                placeName = normalizedPlaceName
            )

        } else {

            ReviewAddress(
                country =
                    rawAddress.countryName
                        ?.takeIf { it.isNotBlank() }
                        ?: baseAddress.country,

                region =
                    listOf(
                        rawAddress.adminArea,
                        rawAddress.subAdminArea
                    )
                        .firstNotBlank()
                        ?: baseAddress.region,

                localityType =
                    detectLocalityType(
                        rawAddress,
                        baseAddress
                    ),

                city =
                    listOf(
                        rawAddress.locality,
                        rawAddress.subLocality,
                        rawAddress.subAdminArea,
                        rawAddress.adminArea
                    )
                        .firstNotBlank()
                        ?: baseAddress.city,

                street =
                    listOf(
                        rawAddress.thoroughfare,
                        rawAddress.subLocality,
                        rawAddress.featureName
                    )
                        .firstNotBlank()
                        ?: baseAddress.street,

                house =
                    listOf(
                        rawAddress.subThoroughfare,
                        rawAddress.premises
                    )
                        .firstNotBlank()
                        ?: baseAddress.house,

                placeName = normalizedPlaceName
            )
        }

    } catch (_: Exception) {

        baseAddress.copy(
            placeName = normalizedPlaceName
        )
    }
}

private fun List<String?>.firstNotBlank(): String? {
    return firstOrNull {
        !it.isNullOrBlank()
    }?.trim()
}

private fun detectLocalityType(
    address: Address,
    fallbackAddress: ReviewAddress
): String {
    return when {
        !address.locality.isNullOrBlank() ->
            "город"

        !address.subAdminArea.isNullOrBlank() ->
            "район"

        else ->
            fallbackAddress.localityType
    }
}