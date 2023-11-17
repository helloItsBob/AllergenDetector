package com.bpr.allergendetector.ui.lists

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bpr.allergendetector.R
import com.bpr.allergendetector.ui.allergenlist.Allergen
import com.bpr.allergendetector.ui.allergenlist.AllergenDB
import com.bpr.allergendetector.ui.allergenlist.AllergenRepo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ListsViewModel(application: Application) : AndroidViewModel(application) {

    private val allergenRepo: AllergenRepo =
        AllergenRepo(AllergenDB.getInstance(application).allergenDAO())
    val allAllergens: LiveData<List<Allergen>> = allergenRepo.getAllAllergens()

    private val productRepo: ProductRepo =
        ProductRepo(ProductDB.getInstance(application).productDAO())
    val allProducts: LiveData<List<Product>> = productRepo.getAllProducts()

    fun insert(product: Product) {
        viewModelScope.launch {
            productRepo.insert(product)
        }
    }

    fun insertAll(products: List<Product>) {
        viewModelScope.launch {
            productRepo.insertAll(products)
        }
    }

    fun update(product: Product) {
        viewModelScope.launch {
            productRepo.update(product)
        }
    }

    fun delete(product: Product) {
        viewModelScope.launch {
            productRepo.delete(product)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            productRepo.deleteAll()
        }
    }

    fun showAddTagDialog(
        context: Context,
        inflater: LayoutInflater,
        chipGroup: ChipGroup,
        view: View
    ) {
        val builder = AlertDialog.Builder(context)
        val dialogView = inflater.inflate(R.layout.add_tag_modal, null)
        val tagInputEditTextDialog =
            dialogView.findViewById<TextInputEditText>(R.id.tagInputEditText)

        builder.setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val tagText = tagInputEditTextDialog.text.toString().trim()
                if (tagText.isNotEmpty()) {
                    val chip = Chip(context)
                    chip.text =
                        getApplication<Application>().getString(R.string.tag_format, tagText)
                    chip.setCloseIconResource(R.drawable.baseline_cancel_24)
                    chip.isCloseIconVisible = true
                    chip.setOnCloseIconClickListener {
                        chipGroup.removeView(chip)
                    }

                    view.findViewById<ChipGroup>(R.id.tagChipGroup).addView(chip)
                } else Toast.makeText(context, "Tag cannot be empty", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    fun getTags(chipGroup: ChipGroup): List<String> {
        val tags: ArrayList<String> = ArrayList()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            tags.add(chip.text.toString())
        }

        return tags
    }

    fun removeTag(chipGroup: ChipGroup, tagText: String) {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.text.toString() == tagText) {
                chipGroup.removeView(chip)
                break
            }
        }
    }

    fun addProductToFireStore(product: Product) {
        val db = FirebaseFirestore.getInstance()

        val saveProduct = product.copy()
        saveProduct.category = wrapInQuotes(saveProduct.category)
        saveProduct.allergens = wrapInQuotes(saveProduct.allergens!!)
        saveProduct.tags = wrapInQuotes(saveProduct.tags!!)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val productCollectionRef = db.collection("users").document(userId)
            // update a document
            productCollectionRef.update(
                "product",
                FieldValue.arrayUnion(saveProduct)
            )
                .addOnSuccessListener {
                    Log.e("ProductList", "Product added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("ProductList", "Error adding product", e)
                }
        }
    }

    fun updateProductInFireStore(
        productName: EditText,
        newProduct: Product
    ) {
        val db = FirebaseFirestore.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val collectionRef = db.collection("users").document(userId)

            collectionRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Retrieve the current list of products
                        val productList =
                            documentSnapshot.get("product") as? MutableList<HashMap<String, Any>>
                                ?: mutableListOf()
                        Log.e("ProductList", productList.toString())

                        // Find the index of the product with the specified name
                        val indexOfProduct =
                            productList.indexOfFirst { it["name"] == productName.text.toString() }

                        if (indexOfProduct != -1) {
                            // Replace the existing product with the new one
                            productList[indexOfProduct] = hashMapOf(
                                "id" to newProduct.id,
                                "name" to newProduct.name,
                                "image" to newProduct.image,
                                "category" to wrapInQuotes(newProduct.category),
                                "harmful" to newProduct.harmful,
                                "allergensJson" to wrapInQuotes(newProduct.allergens!!),
                                "tagsJson" to wrapInQuotes(newProduct.tags!!)
                            )

                            // Update the FireStore document with the modified list
                            collectionRef.update("product", productList)
                                .addOnSuccessListener {
                                    Log.e("ProductList", "Product updated successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("ProductList", "Error updating product", e)
                                }
                        } else {
                            Log.e(
                                "ProductList",
                                "Product with name '${productName.text}' not found"
                            )
                        }
                    } else {
                        Log.e("ProductList", "Document doesn't exist")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProductList", "Error getting document", e)
                }
        }
    }

    fun deleteProductFromFireStore(product: Product) {
        val db = FirebaseFirestore.getInstance()

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val collectionRef = db.collection("users").document(userId)

            collectionRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Retrieve the current list of products
                        val productList =
                            documentSnapshot.get("product") as? MutableList<HashMap<String, Any>>
                                ?: mutableListOf()
                        Log.e("ProductList", productList.toString())

                        // Find the index of the product with the specified name
                        val indexOfProduct = productList.indexOfFirst { it["name"] == product.name }

                        if (indexOfProduct != -1) {
                            // Remove the product from the list
                            productList.removeAt(indexOfProduct)

                            // Update the FireStore document with the modified list
                            collectionRef.update("product", productList)
                                .addOnSuccessListener {
                                    Log.e("ProductList", "Product removed successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("ProductList", "Error removing product", e)
                                }
                        } else {
                            Log.e("ProductList", "Product with name '${product.name}' not found")
                        }
                    } else {
                        Log.e("ProductList", "Document doesn't exist")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProductList", "Error getting document", e)
                }
        }
    }

    private fun wrapInQuotes(originalString: String): String {
        return "\"$originalString\""
    }
}