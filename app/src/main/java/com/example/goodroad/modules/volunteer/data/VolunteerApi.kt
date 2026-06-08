package com.example.goodroad.modules.volunteer.data

import com.example.goodroad.modules.volunteer.data.models.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface VolunteerApi {

    @GET("volunteer/menu")
    suspend fun getMenu(): VolunteerMenuRespDto

    @POST("volunteer/applications")
    suspend fun createApplication(
        @Body req: CreateVolunteerApplicationReqDto
    ): VolunteerApplicationRespDto

    @Multipart
    @POST("volunteer/applications/photos")
    suspend fun uploadCertificate(
        @Part file: MultipartBody.Part
    ): PhotoUploadRespDto

    @GET("volunteer/requests/own")
    suspend fun listOwnRequests(): List<HelpRequestRespDto>

    @POST("volunteer/requests")
    suspend fun createHelpRequest(
        @Body req: HelpRequestCreateReqDto
    ): HelpRequestRespDto

    @GET("volunteer/requests/available")
    suspend fun listAvailableRequests(
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null
    ): List<HelpRequestRespDto>

    @GET("volunteer/requests/my-wards")
    suspend fun listMyWards(): List<HelpRequestRespDto>

    @GET("volunteer/requests/{id}")
    suspend fun getHelpRequest(
        @Path("id") id: String
    ): HelpRequestRespDto

    @POST("volunteer/requests/{id}/accept")
    suspend fun acceptRequest(
        @Path("id") id: String
    ): HelpRequestRespDto

    @POST("volunteer/requests/{id}/withdraw")
    suspend fun withdrawResponse(
        @Path("id") id: String
    ): HelpRequestRespDto

    @POST("volunteer/requests/{id}/cancel")
    suspend fun cancelOwnRequest(
        @Path("id") id: String
    ): HelpRequestRespDto

    @DELETE("volunteer/requests/{id}")
    suspend fun deleteOwnRequest(
        @Path("id") id: String
    )

    @POST("volunteer/requests/{id}/route")
    suspend fun setWalkRoute(
        @Path("id") id: String,
        @Body req: WalkRouteReqDto
    ): HelpRequestRespDto

    @POST("volunteer/requests/{id}/start")
    suspend fun startWalk(
        @Path("id") id: String,
        @Body req: WalkRouteReqDto? = null
    ): HelpRequestRespDto

    @POST("volunteer/requests/{id}/finish")
    suspend fun finishWalk(
        @Path("id") id: String
    ): HelpRequestRespDto

}