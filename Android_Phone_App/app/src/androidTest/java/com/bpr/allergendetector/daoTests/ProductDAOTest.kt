package com.bpr.allergendetector.daoTests

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bpr.allergendetector.ui.lists.Product
import com.bpr.allergendetector.ui.lists.ProductDAO
import com.bpr.allergendetector.ui.lists.ProductDB
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
class ProductDAOTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var productDao: ProductDAO
    private lateinit var productDb: ProductDB

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Use an in-memory database for testing
        productDb = Room.inMemoryDatabaseBuilder(context, ProductDB::class.java)
            .allowMainThreadQueries() // Allows database operations on the main thread (for testing purposes only)
            .build()

        productDao = productDb.productDAO()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        productDb.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertReadDeleteProduct() = runBlocking {
        // Given product
        val product = Product(1, "Milka", "base64string", "Chocolate", false, null, "#choc")

        // When inserting the product into the database
        productDao.insert(product)

        // Observe the LiveData
        val liveData = productDao.getAllProducts()
        val observer = Observer<List<Product>> { productList ->

            assertNotNull(productList)
            assertEquals(1, productList.size)
            assertThat(productList).containsExactly(product)

            val retrievedProduct = productList.single()
            assertEquals(product.id, retrievedProduct.id)
            assertEquals(product.name, retrievedProduct.name)
            assertEquals(product.image, retrievedProduct.image)
            assertEquals(product.category, retrievedProduct.category)
            assertEquals(product.harmful, retrievedProduct.harmful)
            assertEquals(product.allergens, retrievedProduct.allergens)
            assertEquals(product.tags, retrievedProduct.tags)
        }

        // Observe the LiveData and wait for changes
        liveData.observeForever(observer)

        // Clean up the observer
        liveData.removeObserver(observer)

        // When deleting the product from the database
        productDao.delete(product)

        // Observe the LiveData
        val liveData2 = productDao.getAllProducts()
        val observer2 = Observer<List<Product>> { productList ->

            assertNotNull(productList) // list is there but empty
            assertEquals(0, productList.size)
        }

        // Observe the LiveData and wait for changes
        liveData2.observeForever(observer2)

        // Clean up the observer
        liveData2.removeObserver(observer2)
    }

    @Test
    @Throws(Exception::class)
    fun insertReadDeleteProducts() = runBlocking {
        // Given products
        val product1 = Product(1, "Milk", "base64string", "Diary products", true, "milk", "#milk")
        val product2 = Product(2, "Tuc", "base64string", "Snacks", false, null, null)
        val product3 = Product(3, "Ham", "base64string", "Meats", false, null, "#ham")

        // When inserting the products into the database
        val productsToInsert = listOf(product1, product2, product3)
        productDao.insertAll(productsToInsert)

        // Observe the LiveData
        val liveData = productDao.getAllProducts()
        val observer = Observer<List<Product>> { productList ->

            assertNotNull(productList)
            assertEquals(3, productList.size)
            assertThat(productList).containsExactly(product1, product2, product3)

            val retrievedProduct1 = productList[0]
            assertEquals(product1.id, retrievedProduct1.id)
            assertEquals(product1.name, retrievedProduct1.name)
            assertEquals(product1.image, retrievedProduct1.image)
            assertEquals(product1.category, retrievedProduct1.category)
            assertEquals(product1.harmful, retrievedProduct1.harmful)
            assertEquals(product1.allergens, retrievedProduct1.allergens)
            assertEquals(product1.tags, retrievedProduct1.tags)

            val retrievedProduct2 = productList[1]
            assertEquals(product2.id, retrievedProduct2.id)
            assertEquals(product2.name, retrievedProduct2.name)
            assertEquals(product2.image, retrievedProduct2.image)
            assertEquals(product2.category, retrievedProduct2.category)
            assertEquals(product2.harmful, retrievedProduct2.harmful)
            assertEquals(product2.allergens, retrievedProduct2.allergens)
            assertEquals(product2.tags, retrievedProduct2.tags)

            val retrievedProduct3 = productList[2]
            assertEquals(product3.id, retrievedProduct3.id)
            assertEquals(product3.name, retrievedProduct3.name)
            assertEquals(product3.image, retrievedProduct3.image)
            assertEquals(product3.category, retrievedProduct3.category)
            assertEquals(product3.harmful, retrievedProduct3.harmful)
            assertEquals(product3.allergens, retrievedProduct3.allergens)
            assertEquals(product3.tags, retrievedProduct3.tags)
        }

        // Observe the LiveData and wait for changes
        liveData.observeForever(observer)

        // Clean up the observer
        liveData.removeObserver(observer)

        // When deleting the products from the database
        productDao.deleteAll()

        // Observe the LiveData
        val liveData2 = productDao.getAllProducts()
        val observer2 = Observer<List<Product>> { productList ->

            assertNotNull(productList) // list is there but empty
            assertEquals(0, productList.size)
        }

        // Observe the LiveData and wait for changes
        liveData2.observeForever(observer2)

        // Clean up the observer
        liveData2.removeObserver(observer2)
    }

    @Test
    @Throws(Exception::class)
    fun insertReadUpdateProduct() = runBlocking {
        // Given product
        val product = Product(1, "Milka", "base64string", "Chocolate", false, null, "#choc")

        // When inserting the product into the database
        productDao.insert(product)

        // Observe the LiveData
        val liveData = productDao.getAllProducts()
        val observer = Observer<List<Product>> { productList ->

            assertNotNull(productList)
            assertEquals(1, productList.size)
            assertThat(productList).containsExactly(product)

            val retrievedProduct = productList.single()
            assertEquals(product.id, retrievedProduct.id)
            assertEquals(product.name, retrievedProduct.name)
            assertEquals(product.image, retrievedProduct.image)
            assertEquals(product.category, retrievedProduct.category)
            assertEquals(product.harmful, retrievedProduct.harmful)
            assertEquals(product.allergens, retrievedProduct.allergens)
            assertEquals(product.tags, retrievedProduct.tags)
        }

        // Observe the LiveData and wait for changes
        liveData.observeForever(observer)

        // Clean up the observer
        liveData.removeObserver(observer)

        // When updating the product in the database
        val updatedProduct = Product(1, "Milky Way", "base64image", "Sweets & Snacks", true, "nuts", "#milky")
        productDao.update(updatedProduct)

        // Observe the LiveData
        val liveData2 = productDao.getAllProducts()
        val observer2 = Observer<List<Product>> { productList ->

            assertNotNull(productList)
            assertEquals(1, productList.size)
            assertThat(productList).containsExactly(updatedProduct)

            val retrievedProduct = productList.single()
            assertEquals(product.id, updatedProduct.id) // id should not change
            assertEquals(updatedProduct.id, retrievedProduct.id)

            assertNotEquals(product.name, retrievedProduct.name)
            assertEquals(updatedProduct.name, retrievedProduct.name)

            assertNotEquals(product.image, retrievedProduct.image)
            assertEquals(updatedProduct.image, retrievedProduct.image)

            assertNotEquals(product.category, retrievedProduct.category)
            assertEquals(updatedProduct.category, retrievedProduct.category)

            assertNotEquals(product.harmful, retrievedProduct.harmful)
            assertEquals(updatedProduct.harmful, retrievedProduct.harmful)

            assertNotEquals(product.allergens, retrievedProduct.allergens)
            assertEquals(updatedProduct.allergens, retrievedProduct.allergens)

            assertNotEquals(product.tags, retrievedProduct.tags)
            assertEquals(updatedProduct.tags, retrievedProduct.tags)
        }

        // Observe the LiveData and wait for changes
        liveData2.observeForever(observer2)

        // Clean up the observer
        liveData2.removeObserver(observer2)
    }
}