package com.bpr.allergendetector.daoTests

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bpr.allergendetector.ui.SwitchState
import com.bpr.allergendetector.ui.allergenlist.Allergen
import com.bpr.allergendetector.ui.recentscans.AllergenListConverter
import com.bpr.allergendetector.ui.recentscans.RecentScan
import com.bpr.allergendetector.ui.recentscans.RecentScanDAO
import com.bpr.allergendetector.ui.recentscans.RecentScanDB
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecentScanDAOTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var recentScanDao: RecentScanDAO
    private lateinit var recentScanDB: RecentScanDB

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Use an in-memory database for testing
        recentScanDB = Room.inMemoryDatabaseBuilder(context, RecentScanDB::class.java)
            .allowMainThreadQueries() // Allows database operations on the main thread (for testing purposes only)
            .build()

        recentScanDao = recentScanDB.recentScanDAO()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        recentScanDB.close()
    }

    @Test
    @Throws(Exception::class)
    fun testAllergenListConverterFromString() {
        val json =
            "[{\"id\":1,\"name\":\"Peanuts\",\"severity\":3},{\"id\":2,\"name\":\"Dairy\",\"severity\":2}]"
        val expectedList = listOf(
            Allergen(1, "Peanuts", 3),
            Allergen(2, "Dairy", 2),
        )

        val allergenListConverter = AllergenListConverter()
        val convertedList = allergenListConverter.fromString(json)

        assertEquals(expectedList, convertedList)
    }

    @Test
    @Throws(Exception::class)
    fun testAllergenListConverterFromList() {
        val expectedJson =
            "[{\"id\":3,\"name\":\"Corn\",\"severity\":1},{\"id\":4,\"name\":\"Gluten\",\"severity\":2}]"
        val allergenList = listOf(
            Allergen(3, "Corn", 1),
            Allergen(4, "Gluten", 2)
        )

        val allergenListConverter = AllergenListConverter()
        val convertedJson = allergenListConverter.fromList(allergenList)

        assertEquals(expectedJson, convertedJson)
    }

    @Test
    @Throws(Exception::class)
    fun insertReadRecentScan() = runBlocking {
        // Given recent scan
        val allergenList = listOf(Allergen(1, "Peanuts", 3), Allergen(2, "Milk", 2))
        val recentScan = RecentScan(1, "base64string", SwitchState.HARMFUL_STATE, allergenList)

        // When inserting the recent scan into the database
        recentScanDao.insert(recentScan)

        // Get the data
        val recentScanList = recentScanDao.getLatestRecentScans()

        assertNotNull(recentScanList)
        assertEquals(1, recentScanList.size)
        assertThat(recentScanList).containsExactly(recentScan)

        val retrievedRecentScan = recentScanList.single()
        assertEquals(recentScan.id, retrievedRecentScan.id)
        assertEquals(recentScan.image, retrievedRecentScan.image)
        assertEquals(recentScan.result, retrievedRecentScan.result)
        assertEquals(recentScan.allergenList, retrievedRecentScan.allergenList)
    }

    @Test
    @Throws(Exception::class)
    fun insertReadDeleteRecentScans() = runBlocking {
        // Given recent scans
        val recentScan1 = RecentScan(1, "base64string", SwitchState.HARMLESS_STATE, null)
        val recentScan2 = RecentScan(2, "base64string", SwitchState.HARMLESS_STATE, null)
        val recentScan3 = RecentScan(3, "base64string", SwitchState.HARMLESS_STATE, null)

        // When inserting the recent scans into the database
        val recentScansToInsert = listOf(recentScan1, recentScan2, recentScan3)
        recentScanDao.insertAll(recentScansToInsert)

        // Get the data
        val recentScanList = recentScanDao.getLatestRecentScans() // Note: reverse order!

        assertNotNull(recentScanList)
        assertEquals(3, recentScanList.size)
        assertThat(recentScanList).containsExactly(recentScan1, recentScan2, recentScan3)

        val retrievedRecentScan1 = recentScanList[0]
        assertEquals(recentScan3.id, retrievedRecentScan1.id)
        assertEquals(recentScan3.image, retrievedRecentScan1.image)
        assertEquals(recentScan3.result, retrievedRecentScan1.result)
        assertEquals(recentScan3.allergenList, retrievedRecentScan1.allergenList)

        val retrievedRecentScan2 = recentScanList[1]
        assertEquals(recentScan2.id, retrievedRecentScan2.id)
        assertEquals(recentScan2.image, retrievedRecentScan2.image)
        assertEquals(recentScan2.result, retrievedRecentScan2.result)
        assertEquals(recentScan2.allergenList, retrievedRecentScan2.allergenList)

        val retrievedRecentScan3 = recentScanList[2]
        assertEquals(recentScan1.id, retrievedRecentScan3.id)
        assertEquals(recentScan1.image, retrievedRecentScan3.image)
        assertEquals(recentScan1.result, retrievedRecentScan3.result)
        assertEquals(recentScan1.allergenList, retrievedRecentScan3.allergenList)

        // When deleting the recent scans from the database
        recentScanDao.deleteAll()

        // Check if deleted
        val recentScanList2 = recentScanDao.getLatestRecentScans()

        assertNotNull(recentScanList2) // list is there but empty
        assertEquals(0, recentScanList2.size)
    }

    @Test
    @Throws(Exception::class)
    fun insertReadDeleteRecentScansExceptLatestMoreThan10() = runBlocking {
        // Given 15 recent scans
        val recentScansToInsert = mutableListOf<RecentScan>()
        for (i in 1..15) {
            val recentScan =
                RecentScan(i.toLong(), "base64string", SwitchState.HARMLESS_STATE, null)
            recentScansToInsert.add(recentScan)
        }

        // When inserting the recent scans into the database
        recentScanDao.insertAll(recentScansToInsert)

        // Get the data
        val recentScansLatest = recentScanDao.deleteAndRetrieveLatest()

        assertNotNull(recentScansLatest)
        assertEquals(10, recentScansLatest.size)

        recentScansLatest.forEach { recentScan ->
            // make sure that only id's 6-15 are retrieved
            assertEquals(true, recentScan.id in 6..15)
            // and not 1-5
            assertEquals(false, recentScan.id in 1..5)

            // the rest of the data should be the same
            assertEquals("base64string", recentScan.image)
            assertEquals(SwitchState.HARMLESS_STATE, recentScan.result)
            assertEquals(null, recentScan.allergenList)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertReadDeleteRecentScansExceptLatestLessThan10() = runBlocking {
        // Given 5 recent scans
        val recentScansToInsert = mutableListOf<RecentScan>()
        for (i in 1..5) {
            val recentScan =
                RecentScan(i.toLong(), "base64string", SwitchState.HARMLESS_STATE, null)
            recentScansToInsert.add(recentScan)
        }

        // When inserting the recent scans into the database
        recentScanDao.insertAll(recentScansToInsert)

        // Get the data, make sure that no scans are deleted
        val recentScansLatest = recentScanDao.deleteAndRetrieveLatest()

        assertNotNull(recentScansLatest)
        assertEquals(5, recentScansLatest.size)

        recentScansLatest.forEach { recentScan ->
            // make sure that only id's 1-5 are retrieved
            assertEquals(true, recentScan.id in 1..5)

            // the rest of the data should be the same
            assertEquals("base64string", recentScan.image)
            assertEquals(SwitchState.HARMLESS_STATE, recentScan.result)
            assertEquals(null, recentScan.allergenList)
        }
    }
}