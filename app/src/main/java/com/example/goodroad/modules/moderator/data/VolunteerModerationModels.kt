package com.example.goodroad.modules.moderator.data

data class VolunteerApplicationResp(
    val id: String,
    val applicantId: String,
    val applicantName: String,
    val dobroUrl: String?,
    val phone: String,
    val socialNickname: String?,
    val certificatePhotoUrls: List<String> = emptyList(),
    val status: String,
    val moderatorComment: String?,
    val createdAt: String?,
    val moderatedAt: String?
)

data class RejectReq(
    val reason: String
)