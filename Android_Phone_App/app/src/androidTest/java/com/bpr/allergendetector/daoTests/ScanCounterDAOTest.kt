package com.bpr.allergendetector.daoTests

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bpr.allergendetector.ui.statistics.ScanCounter
import com.bpr.allergendetector.ui.statistics.ScanCounterDAO
import com.bpr.allergendetector.ui.statistics.ScanCounterDB
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ScanCounterDAOTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var scanCounterDao: ScanCounterDAO
    private lateinit var scanCounterDb: ScanCounterDB

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Use an in-memory database for testing
        scanCounterDb = Room.inMemoryDatabaseBuilder(context, ScanCounterDB::class.java)
            .allowMainThreadQueries() // Allows database operations on the main thread (for testing purposes only)
            .build()

        scanCounterDao = scanCounterDb.scanCounterDAO()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        scanCounterDb.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertOrUpdateOneAndGetDailyScanCount() = runBlocking {
        // Given scan counter
        val scanCounter = ScanCounter("2023-12-01", 5, 2)

        // When inserting the scan counter into the database
        scanCounterDao.insertOrUpdate(scanCounter)
        val retrievedScanCounter = scanCounterDao.getDailyScanCount("2023-12-01")

        // Check if it exists
        assertNotNull(retrievedScanCounter)
        assertEquals(scanCounter, retrievedScanCounter)
        assertEquals(5, retrievedScanCounter?.count)
        assertEquals(2, retrievedScanCounter?.isHarmful)
    }

    @Test
    @Throws(Exception::class)
    fun insertOrUpdateManyAndGetDailyScanCount() = runBlocking {
        // Given scan counters for the same day
        val scanCounter1 = ScanCounter("2023-12-01", 5, 2)
        val scanCounter2 = ScanCounter("2023-12-01", 8, 3)
        val scanCounter3 = ScanCounter("2023-12-01", 12, 7)

        // When inserting the scan counter into the database
        scanCounterDao.insertOrUpdate(scanCounter1)
        scanCounterDao.insertOrUpdate(scanCounter2)
        scanCounterDao.insertOrUpdate(scanCounter3)

        val retrievedScanCounter = scanCounterDao.getDailyScanCount("2023-12-01")

        // Check if the scan counter has updated to the latest values
        assertNotNull(retrievedScanCounter)

        assertNotEquals(scanCounter1, retrievedScanCounter)
        assertNotEquals(scanCounter2, retrievedScanCounter)
        assertEquals(scanCounter3, retrievedScanCounter)

        assertNotEquals(5, retrievedScanCounter?.count)
        assertNotEquals(2, retrievedScanCounter?.isHarmful)

        assertNotEquals(8, retrievedScanCounter?.count)
        assertNotEquals(3, retrievedScanCounter?.isHarmful)

        assertEquals(12, retrievedScanCounter?.count)
        assertEquals(7, retrievedScanCounter?.isHarmful)
    }

    @Test
    @Throws(Exception::class)
    fun getAllScanCounters() {
        // Given several scan counters per day for different days
        val scanCounters = listOf(
            ScanCounter("2023-12-01", 5, 2),
            ScanCounter("2023-12-01", 6, 3),

            ScanCounter("2023-12-02", 8, 3),
            ScanCounter("2023-12-02", 15, 7),

            ScanCounter("2023-12-03", 8, 7),
            ScanCounter("2023-12-03", 12, 9)
        )

        // When inserting the scan counters into the database
        runBlocking { scanCounterDao.insertAll(scanCounters) }
        val liveDataScanCounters = getValue(scanCounterDao.getAllScanCounters())

        // Make sure that there is only 1 scan counter associated per day
        assertEquals(3, liveDataScanCounters.size)
        assertNotEquals(scanCounters.size, liveDataScanCounters.size)

        // Make sure that the scan counters are the latest ones
        assertEquals(scanCounters[1], liveDataScanCounters[0])
        assertEquals(scanCounters[3], liveDataScanCounters[1])
        assertEquals(scanCounters[5], liveDataScanCounters[2])

        // get the scan counters for specific dates
        runBlocking {
            val scanCountersForDate1 = scanCounterDao.getDailyScanCount("2023-12-01")
            val scanCountersForDate2 = scanCounterDao.getDailyScanCount("2023-12-02")
            val scanCountersForDate3 = scanCounterDao.getDailyScanCount("2023-12-03")

            // check if they have the correct values
            assertEquals(6, scanCountersForDate1?.count)
            assertEquals(3, scanCountersForDate1?.isHarmful)

            assertEquals(15, scanCountersForDate2?.count)
            assertEquals(7, scanCountersForDate2?.isHarmful)

            assertEquals(12, scanCountersForDate3?.count)
            assertEquals(9, scanCountersForDate3?.isHarmful)
        }

    }

    @Test
    @Throws(Exception::class)
    fun deleteAllAndGetAllScanCounters() = runBlocking {
        // Given list of scan counters
        val scanCounters = listOf(
            ScanCounter("2023-12-01", 5, 2),
            ScanCounter("2023-12-02", 8, 3),
            ScanCounter("2023-12-03", 12, 7)
        )

        // When inserting the scan counters into the database and deleting them
        runBlocking {
            scanCounterDao.insertAll(scanCounters)
            scanCounterDao.deleteAll()
        }
        val liveDataScanCounters = getValue(scanCounterDao.getAllScanCounters())

        // Check if deleted, list should be empty
        assertTrue(liveDataScanCounters.isEmpty())

        // get the scan counters for specific dates
        runBlocking {
            val scanCountersForDate1 = scanCounterDao.getDailyScanCount("2023-12-01")
            val scanCountersForDate2 = scanCounterDao.getDailyScanCount("2023-12-02")
            val scanCountersForDate3 = scanCounterDao.getDailyScanCount("2023-12-03")

            // check if they are null
            assertEquals(null, scanCountersForDate1)
            assertEquals(null, scanCountersForDate2)
            assertEquals(null, scanCountersForDate3)
        }
    }

    // helper function to get values from LiveData objects
    @Throws(InterruptedException::class)
    private fun <T> getValue(liveData: LiveData<T>): T {
        var value: T? = null
        val latch = CountDownLatch(1)

        val observer = Observer<T> {
            value = it
            latch.countDown()
        }

        liveData.observeForever(observer)

        latch.await(2, TimeUnit.SECONDS)

        liveData.removeObserver(observer)

        return value!!
    }
}