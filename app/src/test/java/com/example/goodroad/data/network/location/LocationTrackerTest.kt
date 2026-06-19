package com.example.goodroad.data.network.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class LocationTrackerTest {

    private lateinit var context: Context
    private lateinit var locationTracker: LocationTracker
    private lateinit var mockFusedLocationClient: FusedLocationProviderClient

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        mockFusedLocationClient = mockk(relaxed = true)

        mockkStatic(LocationServices::class)
        every { LocationServices.getFusedLocationProviderClient(any()) } returns mockFusedLocationClient

        mockkStatic(ContextCompat::class)
        every { ContextCompat.checkSelfPermission(any(), any()) } returns PackageManager.PERMISSION_GRANTED

        locationTracker = LocationTracker(context)
    }

    @Test
    fun hasPermissionsShouldReturnTrue_whenPermissionsGranted() {
        every { ContextCompat.checkSelfPermission(any(), eq(Manifest.permission.ACCESS_FINE_LOCATION)) } returns PackageManager.PERMISSION_GRANTED

        val result = locationTracker.hasPermissions()

        assertTrue(result)
    }

    @Test
    fun hasPermissionSshouldReturnFalse_whenPermissionsDenied() {
        every { ContextCompat.checkSelfPermission(any(), eq(Manifest.permission.ACCESS_FINE_LOCATION)) } returns PackageManager.PERMISSION_DENIED
        every { ContextCompat.checkSelfPermission(any(), eq(Manifest.permission.ACCESS_COARSE_LOCATION)) } returns PackageManager.PERMISSION_DENIED

        val result = locationTracker.hasPermissions()

        assertFalse(result)
    }

    @Test
    fun getCurrentLocatioSshouldReturnNull_whenNoPermissions() = runBlocking {
        every { ContextCompat.checkSelfPermission(any(), eq(Manifest.permission.ACCESS_FINE_LOCATION)) } returns PackageManager.PERMISSION_DENIED

        val result = locationTracker.getCurrentLocation()

        assertNull(result)
    }

    @Test
    fun locationUpdates_shouldReturnFlow() = runBlocking {
        val flow = locationTracker.locationUpdates()

        assertNotNull(flow)
    }
}