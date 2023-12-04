package com.bpr.allergendetector.daoTests

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bpr.allergendetector.ui.allergenlist.Allergen
import com.bpr.allergendetector.ui.allergenlist.AllergenDAO
import com.bpr.allergendetector.ui.allergenlist.AllergenDB
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AllergenDAOTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var allergenDao: AllergenDAO
    private lateinit var allergenDb: AllergenDB

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Use an in-memory database for testing
        allergenDb = Room.inMemoryDatabaseBuilder(context, AllergenDB::class.java)
            .allowMainThreadQueries() // Allows database operations on the main thread (for testing purposes only)
            .build()

        allergenDao = allergenDb.allergenDAO()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        allergenDb.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertReadDeleteAllergen() = runBlocking {
        // Given allergen
        val allergen = Allergen(1, "Peanuts", 3)

        // When inserting the allergen into the database
        allergenDao.insert(allergen)

        // Observe the LiveData
        val liveData = allergenDao.getAllAllergens()
        val observer = Observer<List<Allergen>> { allergenList ->

            assertNotNull(allergenList)
            assertEquals(1, allergenList.size)
            assertThat(allergenList).containsExactly(allergen)

            val retrievedAllergen = allergenList.single()
            assertEquals(allergen.id, retrievedAllergen.id)
            assertEquals(allergen.name, retrievedAllergen.name)
            assertEquals(allergen.severity, retrievedAllergen.severity)
        }

        // Observe the LiveData and wait for changes
        liveData.observeForever(observer)

        // Clean up the observer
        liveData.removeObserver(observer)

        // When deleting the allergen from the database
        allergenDao.delete(allergen)

        // Observe the LiveData
        val liveData2 = allergenDao.getAllAllergens()
        val observer2 = Observer<List<Allergen>> { allergenList ->

            assertNotNull(allergenList) // list is there but empty
            assertEquals(0, allergenList.size)
        }

        // Observe the LiveData and wait for changes
        liveData2.observeForever(observer2)

        // Clean up the observer
        liveData2.removeObserver(observer2)
    }

    @Test
    @Throws(Exception::class)
    fun insertReadDeleteAllergens() = runBlocking {
        // Given allergens
        val allergen1 = Allergen(1, "Peanuts", 3)
        val allergen2 = Allergen(2, "Eggs", 2)
        val allergen3 = Allergen(3, "Milk", 1)

        // When inserting the allergens into the database
        val allergensToInsert = listOf(allergen1, allergen2, allergen3)
        allergenDao.insertAll(allergensToInsert)

        // Observe the LiveData
        val liveData = allergenDao.getAllAllergens()
        val observer = Observer<List<Allergen>> { allergenList ->

            assertNotNull(allergenList)
            assertEquals(3, allergenList.size)
            assertThat(allergenList).containsExactly(allergen1, allergen2, allergen3)

            val retrievedAllergen1 = allergenList[0]
            assertEquals(allergen1.id, retrievedAllergen1.id)
            assertEquals(allergen1.name, retrievedAllergen1.name)
            assertEquals(allergen1.severity, retrievedAllergen1.severity)

            val retrievedAllergen2 = allergenList[1]
            assertEquals(allergen2.id, retrievedAllergen2.id)
            assertEquals(allergen2.name, retrievedAllergen2.name)
            assertEquals(allergen2.severity, retrievedAllergen2.severity)

            val retrievedAllergen3 = allergenList[2]
            assertEquals(allergen3.id, retrievedAllergen3.id)
            assertEquals(allergen3.name, retrievedAllergen3.name)
            assertEquals(allergen3.severity, retrievedAllergen3.severity)
        }

        // Observe the LiveData and wait for changes
        liveData.observeForever(observer)

        // Clean up the observer
        liveData.removeObserver(observer)

        // When deleting the allergens from the database
        allergenDao.deleteAll()

        // Observe the LiveData
        val liveData2 = allergenDao.getAllAllergens()
        val observer2 = Observer<List<Allergen>> { allergenList ->

            assertNotNull(allergenList) // list is there but empty
            assertEquals(0, allergenList.size)
        }

        // Observe the LiveData and wait for changes
        liveData2.observeForever(observer2)

        // Clean up the observer
        liveData2.removeObserver(observer2)
    }

    @Test
    @Throws(Exception::class)
    fun insertReadUpdateAllergen() = runBlocking {
        // Given allergen
        val allergen = Allergen(1, "Peanuts", 3)

        // When inserting the allergen into the database
        allergenDao.insert(allergen)

        // Observe the LiveData
        val liveData = allergenDao.getAllAllergens()
        val observer = Observer<List<Allergen>> { allergenList ->

            assertNotNull(allergenList)
            assertEquals(1, allergenList.size)
            assertThat(allergenList).containsExactly(allergen)

            val retrievedAllergen = allergenList.single()
            assertEquals(allergen.id, retrievedAllergen.id)
            assertEquals(allergen.name, retrievedAllergen.name)
            assertEquals(allergen.severity, retrievedAllergen.severity)
        }

        // Observe the LiveData and wait for changes
        liveData.observeForever(observer)

        // Clean up the observer
        liveData.removeObserver(observer)

        // When updating the allergen in the database
        val updatedAllergen = Allergen(1, "Peanut", 1)
        allergenDao.update(updatedAllergen)

        // Observe the LiveData
        val liveData2 = allergenDao.getAllAllergens()
        val observer2 = Observer<List<Allergen>> { allergenList ->

            assertNotNull(allergenList)
            assertEquals(1, allergenList.size)
            assertThat(allergenList).containsExactly(updatedAllergen)

            val retrievedAllergen = allergenList.single()
            assertEquals(allergen.id, updatedAllergen.id) // id should not change
            assertEquals(updatedAllergen.id, retrievedAllergen.id)

            assertNotEquals(allergen.name, retrievedAllergen.name)
            assertEquals(updatedAllergen.name, retrievedAllergen.name)

            assertNotEquals(allergen.severity, retrievedAllergen.severity)
            assertEquals(updatedAllergen.severity, retrievedAllergen.severity)
        }

        // Observe the LiveData and wait for changes
        liveData2.observeForever(observer2)

        // Clean up the observer
        liveData2.removeObserver(observer2)
    }
}