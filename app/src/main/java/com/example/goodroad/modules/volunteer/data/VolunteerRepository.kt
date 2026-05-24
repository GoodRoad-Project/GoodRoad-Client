package com.example.goodroad.modules.volunteer.data

import com.example.goodroad.modules.volunteer.data.models.*
import okhttp3.MultipartBody

class VolunteerRepository(
    private val api: VolunteerApi
) {

    suspend fun getMenu() = api.getMenu()

    suspend fun createApplication(
        dobroUrl: String,
        phone: String,
        socialNickname: String?,
        certificatePhotoUrls: List<String>?
    ) = api.createApplication(
        CreateVolunteerApplicationReqDto(
            dobroUrl = dobroUrl,
            phone = phone,
            socialNickname = socialNickname,
            certificatePhotoUrls = certificatePhotoUrls
        )
    )

    suspend fun uploadCertificate(file: MultipartBody.Part) =
        api.uploadCertificate(file)

    suspend fun loadOwnRequests(): List<HelpRequestItem> =
        api.listOwnRequests().map { it.toUi() }

    suspend fun createHelpRequest(
        fromAddress: String,
        toAddress: String,
        date: String,
        time: String,
        phone: String,
        socialNickname: String?,
        comment: String
    ): HelpRequestItem {
        val created = api.createHelpRequest(
            HelpRequestCreateReqDto(
                fromAddress = fromAddress,
                toAddress = toAddress,
                date = date,
                time = time,
                phone = phone,
                socialNickname = socialNickname,
                comment = comment
            )
        )
        return created.toUi()
    }

    suspend fun loadAvailableRequests(
        latitude: Double? = null,
        longitude: Double? = null
    ): List<HelpRequestItem> =
        api.listAvailableRequests(latitude, longitude).map { it.toUi() }

    suspend fun loadMyWards(): List<HelpRequestItem> =
        api.listMyWards().map { it.toUi() }

    suspend fun getRequest(id: String): HelpRequestItem =
        api.getHelpRequest(id).toUi()

    suspend fun acceptRequest(id: String): HelpRequestItem =
        api.acceptRequest(id).toUi()

    suspend fun withdrawResponse(id: String): HelpRequestItem =
        api.withdrawResponse(id).toUi()

    suspend fun cancelOwnRequest(id: String): HelpRequestItem =
        api.cancelOwnRequest(id).toUi()

    suspend fun deleteOwnRequest(id: String) {
        api.deleteOwnRequest(id)
    }

    suspend fun setWalkRoute(
        id: String,
        points: String? = null,
        routePoints: List<RoutePointReqDto>? = null
    ): HelpRequestItem =
        api.setWalkRoute(id, WalkRouteReqDto(points = points, routePoints = routePoints)).toUi()

    suspend fun startWalk(
        id: String,
        points: String? = null,
        routePoints: List<RoutePointReqDto>? = null
    ): HelpRequestItem =
        api.startWalk(id, WalkRouteReqDto(points = points, routePoints = routePoints)).toUi()

    suspend fun finishWalk(id: String): HelpRequestItem =
        api.finishWalk(id).toUi()
}