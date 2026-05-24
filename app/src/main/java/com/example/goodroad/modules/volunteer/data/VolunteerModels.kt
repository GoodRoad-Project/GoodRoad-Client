package com.example.goodroad.modules.volunteer.data.models

data class VolunteerMenuRespDto(
    val volunteer: Boolean,
    val applicationStatus: String?,
    val rejectReason: String?
)

data class CreateVolunteerApplicationReqDto(
    val dobroUrl: String,
    val phone: String,
    val socialNickname: String?,
    val certificatePhotoUrls: List<String>?
)

data class PhotoUploadRespDto(
    val photoUrl: String
)

data class VolunteerApplicationRespDto(
    val id: String?,
    val applicantId: String,
    val applicantName: String,
    val dobroUrl: String,
    val phone: String,
    val socialNickname: String?,
    val certificatePhotoUrls: List<String>?,
    val status: String,
    val moderatorComment: String?,
    val createdAt: String?,
    val moderatedAt: String?
)

data class RejectApplicationReqDto(
    val reason: String
)

data class HelpRequestCreateReqDto(
    val fromAddress: String,
    val toAddress: String,
    val date: String,
    val time: String,
    val phone: String,
    val socialNickname: String?,
    val comment: String
)

data class HelpRequestRespDto(
    val id: String?,
    val requesterId: String,
    val volunteerId: String?,
    val fromAddress: String,
    val toAddress: String,
    val date: String,
    val time: String,
    val phone: String?,
    val socialNickname: String?,
    val comment: String?,
    val status: String,
    val contactsVisible: Boolean,
    val canStart: Boolean,
    val started: Boolean,
    val completed: Boolean,
    val createdAt: String?
)

data class RoutePointReqDto(
    val latitude: Double,
    val longitude: Double
)

data class WalkRouteReqDto(
    val points: String? = null,
    val routePoints: List<RoutePointReqDto>? = null
)

/**
 * То, что удобно показывать на экране "Мои заявки".
 * Поля оставлены близкими к твоему текущему UI, чтобы меньше менять экран.
 */
data class HelpRequestItem(
    val id: String,
    val routeStart: String,
    val routeEnd: String,
    val dateTime: String,
    val contact: String,
    val specialNotes: String,
    val comment: String,
    val status: RequestStatus
)

enum class RequestStatus {
    OPEN,
    ACCEPTED,
    CANCELLED,
    COMPLETED,
    UNKNOWN
}

fun String?.toRequestStatus(): RequestStatus = when (this?.uppercase()) {
    "OPEN" -> RequestStatus.OPEN
    "ACCEPTED" -> RequestStatus.ACCEPTED
    "CANCELLED" -> RequestStatus.CANCELLED
    "COMPLETED" -> RequestStatus.COMPLETED
    else -> RequestStatus.UNKNOWN
}

fun HelpRequestRespDto.toUi(): HelpRequestItem {
    val requestId = id ?: ""
    return HelpRequestItem(
        id = requestId,
        routeStart = fromAddress,
        routeEnd = toAddress,
        dateTime = "$date $time",
        contact = phone ?: socialNickname.orEmpty(),
        specialNotes = socialNickname.orEmpty(),
        comment = comment.orEmpty(),
        status = status.toRequestStatus()
    )
}

fun CreateHelpRequestReqDto.toCreateDto() = HelpRequestCreateReqDto(
    fromAddress = fromAddress,
    toAddress = toAddress,
    date = date,
    time = time,
    phone = phone,
    socialNickname = socialNickname,
    comment = comment
)

data class CreateHelpRequestReqDto(
    val fromAddress: String,
    val toAddress: String,
    val date: String,
    val time: String,
    val phone: String,
    val socialNickname: String?,
    val comment: String
)