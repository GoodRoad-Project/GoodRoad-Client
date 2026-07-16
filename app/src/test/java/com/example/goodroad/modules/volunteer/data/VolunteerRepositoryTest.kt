package com.example.goodroad.modules.volunteer.data

import com.example.goodroad.modules.volunteer.data.models.*
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Test

class VolunteerRepositoryTest {

    @Test
    fun getMenuReturnsResponse() = runBlocking {
        val menu = VolunteerMenuRespDto(true, "APPROVED", null)
        val api = FakeVolunteerApi(menuResp = menu)

        val repository = VolunteerRepository(api)

        val result = repository.getMenu()

        assertEquals(menu, result)
    }

    @Test
    fun createApplicationSendsRequest() = runBlocking {
        val req = CreateVolunteerApplicationReqDto(
            dobroUrl = "url",
            phone = "+79990000001",
            socialNickname = "nick",
            certificatePhotoUrls = listOf("a", "b")
        )

        val api = FakeVolunteerApi(
            appResp = VolunteerApplicationRespDto(
                id = "1",
                applicantId = "10",
                applicantName = "Ivan",
                dobroUrl = req.dobroUrl,
                phone = req.phone,
                socialNickname = req.socialNickname,
                certificatePhotoUrls = req.certificatePhotoUrls,
                status = "PENDING",
                moderatorComment = null,
                createdAt = null,
                moderatedAt = null
            )
        )

        val repository = VolunteerRepository(api)

        val result = repository.createApplication(
            req.dobroUrl,
            req.phone,
            req.socialNickname,
            req.certificatePhotoUrls.orEmpty()
        )

        assertEquals("1", result.id)
        assertEquals(req.dobroUrl, api.createReq?.dobroUrl)
        assertEquals(req.phone, api.createReq?.phone)
    }

    @Test
    fun uploadCertificateReturnsUrl() = runBlocking {
        val api = FakeVolunteerApi(uploadResp = PhotoUploadRespDto("url"))
        val repository = VolunteerRepository(api)

        val result = repository.uploadCertificate(mockMultipart())

        assertEquals("url", result.photoUrl)
    }

    @Test
    fun loadOwnRequestsMapsToUi() = runBlocking {
        val api = FakeVolunteerApi(
            ownRequests = listOf(
                HelpRequestRespDto(
                    id = "1",
                    requesterId = "10",
                    volunteerId = null,
                    fromAddress = "A",
                    toAddress = "B",
                    date = "2026-01-01",
                    time = "10:00",
                    phone = "+71234567888",
                    socialNickname = "nick",
                    comment = "c",
                    status = "OPEN",
                    contactsVisible = true,
                    canStart = true,
                    started = false,
                    completed = false,
                    createdAt = null
                )
            )
        )

        val repository = VolunteerRepository(api)

        val result = repository.loadOwnRequests()

        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }

    @Test
    fun createHelpRequestReturnsMappedItem() = runBlocking {
        val resp = helpResp("99")
        val api = FakeVolunteerApi(createHelpResp = resp)

        val repository = VolunteerRepository(api)

        val result = repository.createHelpRequest(
            "A", "B", "2026-01-01", "10:00",
            "+1", "nick", "c"
        )

        assertEquals("99", result.id)
        assertEquals("A", api.helpCreateReq?.fromAddress)
    }

    @Test
    fun deleteOwnRequestSendsId() = runBlocking {
        val api = FakeVolunteerApi()
        val repository = VolunteerRepository(api)

        repository.deleteOwnRequest("123")

        assertEquals("123", api.deletedId)
    }

    @Test
    fun finishWalkReturnsMappedItem() = runBlocking {
        val api = FakeVolunteerApi(finishResp = helpResp("5"))
        val repository = VolunteerRepository(api)

        val result = repository.finishWalk("5")

        assertEquals("5", result.id)
    }


    private class FakeVolunteerApi(
        private val menuResp: VolunteerMenuRespDto =
            VolunteerMenuRespDto(true, null, null),

        private val appResp: VolunteerApplicationRespDto =
            VolunteerApplicationRespDto(
                id = "1",
                applicantId = "10",
                applicantName = "Ivan",
                dobroUrl = "",
                phone = "",
                socialNickname = null,
                certificatePhotoUrls = null,
                status = "PENDING",
                moderatorComment = null,
                createdAt = null,
                moderatedAt = null
            ),

        private val uploadResp: PhotoUploadRespDto =
            PhotoUploadRespDto("url"),

        private val ownRequests: List<HelpRequestRespDto> = emptyList(),

        private val createHelpResp: HelpRequestRespDto =
            helpResp("1"),

        private val finishResp: HelpRequestRespDto =
            helpResp("1")
    ) : VolunteerApi {

        var createReq: CreateVolunteerApplicationReqDto? = null
        var helpCreateReq: HelpRequestCreateReqDto? = null
        var deletedId: String? = null

        override suspend fun getMenu() = menuResp

        override suspend fun createApplication(req: CreateVolunteerApplicationReqDto): VolunteerApplicationRespDto {
            createReq = req
            return appResp
        }

        override suspend fun uploadCertificate(file: MultipartBody.Part): PhotoUploadRespDto =
            uploadResp

        override suspend fun listOwnRequests() = ownRequests

        override suspend fun createHelpRequest(req: HelpRequestCreateReqDto): HelpRequestRespDto {
            helpCreateReq = req
            return createHelpResp
        }

        override suspend fun listAvailableRequests(latitude: Double?, longitude: Double?) =
            emptyList<HelpRequestRespDto>()

        override suspend fun listMyWards() = emptyList<HelpRequestRespDto>()

        override suspend fun getHelpRequest(id: String) = helpResp(id)

        override suspend fun acceptRequest(id: String) = helpResp(id)

        override suspend fun withdrawResponse(id: String) = helpResp(id)

        override suspend fun cancelOwnRequest(id: String) = helpResp(id)

        override suspend fun deleteOwnRequest(id: String) {
            deletedId = id
        }

        override suspend fun setWalkRoute(id: String, req: WalkRouteReqDto) =
            helpResp(id)

        override suspend fun startWalk(id: String, req: WalkRouteReqDto?) =
            helpResp(id)

        override suspend fun finishWalk(id: String) =
            finishResp
    }

    companion object {

        private fun helpResp(id: String) = HelpRequestRespDto(
            id = id,
            requesterId = "10",
            volunteerId = null,
            fromAddress = "A",
            toAddress = "B",
            date = "2026-01-01",
            time = "10:00",
            phone = "+71234567888",
            socialNickname = "nick",
            comment = "c",
            status = "OPEN",
            contactsVisible = true,
            canStart = true,
            started = false,
            completed = false,
            createdAt = null
        )

        private fun mockMultipart(): MultipartBody.Part {
            val body = "img".toRequestBody()
            return MultipartBody.Part.createFormData("file", "a.png", body)
        }
    }
}