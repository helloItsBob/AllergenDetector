package com.bpr.allergendetector.ui.lists

import androidx.lifecycle.LiveData

class ProductRepo(private val productDAO: ProductDAO) {

    private val allProducts: LiveData<List<Product>> = productDAO.getAllProducts()

    fun getAllProducts(): LiveData<List<Product>> {
        return allProducts
    }

    suspend fun insert(product: Product) {
        productDAO.insert(product)
    }

    suspend fun insertAll(products: List<Product>) {
        productDAO.insertAll(products)
    }

    suspend fun update(product: Product) {
        productDAO.update(product)
    }

    suspend fun delete(product: Product) {
        productDAO.delete(product)
    }

    suspend fun deleteAll() {
        productDAO.deleteAll()
    }
}