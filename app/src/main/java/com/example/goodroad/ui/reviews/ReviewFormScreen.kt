package com.example.goodroad.ui.reviews

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.goodroad.data.review.*
import com.example.goodroad.ui.auth.*
import com.example.goodroad.ui.theme.*
import com.example.goodroad.ui.user.UserDecor
import com.example.goodroad.ui.viewmodel.ReviewsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun ReviewFormScreen(
    reviewsViewModel: ReviewsViewModel,
    initialReview: ReviewCardResp?,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isEdit = initialReview != null
    val reviewKey = initialReview?.id ?: "new"

    var placeName by remember(reviewKey) { mutableStateOf(initialReview?.address?.placeName ?: "") }
    var latitude by remember(reviewKey) { mutableStateOf(initialReview?.latitude?.toString() ?: "") }
    var longitude by remember(reviewKey) { mutableStateOf(initialReview?.longitude?.toString() ?: "") }
    var rating by remember(reviewKey) { mutableStateOf(initialReview?.rating?.toInt()) }
    var comment by remember(reviewKey) { mutableStateOf(initialReview?.comment ?: "") }
    var formError by remember(reviewKey) { mutableStateOf<String?>(null) }
    val photoUrls = remember(reviewKey) {
        mutableStateListOf<String>().apply {
            addAll(initialReview?.photoUrls.orEmpty())
        }
    }

    val obstacleSeverities = remember(reviewKey) {
        mutableStateMapOf<String, Int>().apply {
            ReviewObstacleTypes.forEach { type ->
                val current = initialReview?.obstacles
                    ?.firstOrNull { it.obstacleType == type }
                    ?.severity
                    ?.toInt()
                    ?: 0
                put(type, current)
            }
        }
    }

    val isSubmitting by reviewsViewModel.isSubmitting
    val isPhotoUploading by reviewsViewModel.isPhotoUploading
    val serverError by reviewsViewModel.errorMessage

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            reviewsViewModel.uploadReviewPhotos(context, uris) { uploadedUrls ->
                photoUrls.addAll(uploadedUrls)
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
            UserDecor()

            Text(
                text = if (isEdit) "Редактирование отзыва" else "Новый отзыв",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(20.dp))

            PlainField(
                value = placeName,
                onValueChange = { placeName = it },
                label = "Название места"
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Название места можно не заполнять.",
                style = MaterialTheme.typography.bodySmall,
                color = UrbanBrown
            )

            Spacer(Modifier.height(16.dp))

            PlainField(
                value = latitude,
                onValueChange = { latitude = it },
                label = "Широта",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(Modifier.height(12.dp))

            PlainField(
                value = longitude,
                onValueChange = { longitude = it },
                label = "Долгота",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Адрес будет определен автоматически по введенным координатам.",
                style = MaterialTheme.typography.bodySmall,
                color = UrbanBrown
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Координаты должны быть в числовом формате. Можно использовать точку или запятую.",
                style = MaterialTheme.typography.bodySmall,
                color = UrbanBrown
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Оценка",
                style = MaterialTheme.typography.titleMedium,
                color = UrbanBrown
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
                color = UrbanBrown
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "0 — нет такого препятствия, 1 — слабая тяжесть, 2 — средняя тяжесть, 3 — сильная тяжесть.",
                style = MaterialTheme.typography.bodySmall,
                color = UrbanBrown
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Хотя бы у одного препятствия тяжесть должна быть больше 0.",
                style = MaterialTheme.typography.bodySmall,
                color = UrbanBrown
            )

            ReviewObstacleTypes.forEach { type ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = obstacleLabel(type),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(6.dp))
                SeveritySelector(
                    value = obstacleSeverities[type] ?: 0,
                    range = 0..3,
                    onValueChange = { obstacleSeverities[type] = it }
                )
            }

            Spacer(Modifier.height(20.dp))

            TextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                label = {
                    Text(
                        text = "Комментарий",
                        color = UrbanBrown
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
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
                onClick = { photoPickerLauncher.launch("image/*") },
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
                    imageVector = Icons.Default.Photo,
                    contentDescription = null,
                    tint = UrbanBrown
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (isPhotoUploading) "Загружаем фото..." else "Добавить фотографии",
                    color = UrbanBrown
                )
            }

            Spacer(Modifier.height(12.dp))

            ReviewPhotosStrip(
                photoUrls = photoUrls,
                onRemove = { photoUrls.remove(it) }
            )

            AuthStatusText(formError ?: serverError)

            Spacer(Modifier.height(20.dp))

            AuthButton(
                text = when {
                    isPhotoUploading -> "Загружаем фото..."
                    isSubmitting -> "Сохраняем..."
                    isEdit -> "Сохранить изменения"
                    else -> "Отправить отзыв"
                },
                enabled = !isSubmitting && !isPhotoUploading
            ) {
                scope.launch {
                    val validationError = validateReviewForm(
                        latitude = latitude,
                        longitude = longitude,
                        rating = rating,
                        obstacleSeverities = obstacleSeverities
                    )

                    if (validationError != null) {
                        formError = validationError
                        return@launch
                    }

                    val lat = latitude.trim().replace(',', '.').toDouble()
                    val lon = longitude.trim().replace(',', '.').toDouble()

                    val generatedAddress = resolveReviewAddress(
                        context = context,
                        latitude = lat,
                        longitude = lon,
                        placeName = placeName,
                        fallbackAddress = initialReview?.address
                    )

                    formError = null
                    reviewsViewModel.clearMessages()

                    val request = UpsertReviewReq(
                        latitude = lat,
                        longitude = lon,
                        address = generatedAddress,
                        rating = rating!!.toShort(),
                        obstacles = ReviewObstacleTypes.map { type ->
                            ReviewObstacle(
                                obstacleType = type,
                                severity = (obstacleSeverities[type] ?: 0).toShort()
                            )
                        },
                        comment = comment.trim().ifBlank { null },
                        photoUrls = photoUrls.toList()
                    )

                    if (isEdit) {
                        reviewsViewModel.updateReview(initialReview!!.id, request, onSaved)
                    } else {
                        reviewsViewModel.createReview(request, onSaved)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            AuthButton(
                text = "Назад к отзывам",
                backgroundColor = UrbanBrown
            ) {
                onBack()
            }
        }
    }
}

private fun validateReviewForm(
    latitude: String,
    longitude: String,
    rating: Int?,
    obstacleSeverities: Map<String, Int>
): String? {
    val lat = latitude.trim().replace(',', '.').toDoubleOrNull()
    val lon = longitude.trim().replace(',', '.').toDoubleOrNull()
    if (lat == null || lon == null) {
        return "Введите корректные координаты"
    }
    if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) {
        return "Координаты выходят за допустимый диапазон"
    }
    if (rating == null) {
        return "Поставьте оценку отзыву"
    }
    if (obstacleSeverities.values.none { it > 0 }) {
        return "Укажите тяжесть хотя бы для одного препятствия"
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
    val normalizedPlaceName = placeName.trim().ifBlank { null }
    val baseAddress = fallbackAddress ?: ReviewAddress(
        country = "Россия",
        region = "Регион не указан",
        localityType = "город",
        city = "Населенный пункт не указан",
        street = "Улица не указана",
        house = "Без номера",
        placeName = normalizedPlaceName
    )
    val geocoder = Geocoder(context, Locale("ru"))

    return@withContext try {
        val rawAddress = geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
        if (rawAddress == null) {
            baseAddress.copy(placeName = normalizedPlaceName)
        } else {
            ReviewAddress(
                country = rawAddress.countryName?.takeIf { it.isNotBlank() }
                    ?: baseAddress.country,
                region = listOf(rawAddress.adminArea, rawAddress.subAdminArea)
                    .firstNotBlank()
                    ?: baseAddress.region,
                localityType = detectLocalityType(rawAddress, baseAddress),
                city = listOf(rawAddress.locality, rawAddress.subLocality, rawAddress.subAdminArea, rawAddress.adminArea)
                    .firstNotBlank()
                    ?: baseAddress.city,
                street = listOf(rawAddress.thoroughfare, rawAddress.subLocality, rawAddress.featureName)
                    .firstNotBlank()
                    ?: baseAddress.street,
                house = listOf(rawAddress.subThoroughfare, rawAddress.premises)
                    .firstNotBlank()
                    ?: baseAddress.house,
                placeName = normalizedPlaceName
            )
        }
    } catch (_: Exception) {
        baseAddress.copy(placeName = normalizedPlaceName)
    }
}

private fun List<String?>.firstNotBlank(): String? {
    return firstOrNull { !it.isNullOrBlank() }?.trim()
}

private fun detectLocalityType(address: Address, fallbackAddress: ReviewAddress): String {
    return when {
        !address.locality.isNullOrBlank() -> "город"
        !address.subAdminArea.isNullOrBlank() -> "район"
        else -> fallbackAddress.localityType
    }
}
