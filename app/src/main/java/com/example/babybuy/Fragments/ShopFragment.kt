package com.example.babybuy.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babybuy.AppConstant
import com.example.babybuy.Dashboard.AddOrUpdateItemActivity
import com.example.babybuy.Dashboard.ProductRecyclerAdapter
import com.example.babybuy.DetailViewActivity
import com.example.babybuy.UiUtility
import com.example.babybuy.databinding.FragmentShopBinding
import com.example.babybuy.model.FirestoreProduct
import com.example.babybuy.model.Product
import com.google.firebase.firestore.FirebaseFirestore

class ShopFragment : Fragment(), ProductRecyclerAdapter.ProductAdapterListener {
    private lateinit var shopBinding: FragmentShopBinding
    private lateinit var productRecyclerAdapter: ProductRecyclerAdapter

    private lateinit var startAddOrUpdateActivityForResult: ActivityResultLauncher<Intent>
    private lateinit var startDetailViewActivity: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the launcher for adding/updating items
        startAddOrUpdateActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AddOrUpdateItemActivity.RESULT_CODE_COMPLETE) {
                setUpRecyclerView()
            } else {
                // TODO: Do nothing
            }
        }

        // Register the launcher for detail view
        startDetailViewActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == DetailViewActivity.RESULT_CODE_REFRESH) {
                setUpRecyclerView()
            } else {
                // Do nothing
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        shopBinding = FragmentShopBinding.inflate(inflater, container, false)
        setUpViews()
        return shopBinding.root
    }

    private fun setUpViews() {
        setUpFloatingActionButton()
        setUpRecyclerView()
    }

    private fun setUpFloatingActionButton() {
        shopBinding.fabAdd.setOnClickListener {
            val intent = Intent(requireActivity(), AddOrUpdateItemActivity::class.java)
            startAddOrUpdateActivityForResult.launch(intent)
        }
    }

    private fun setUpRecyclerView() {
        val db = FirebaseFirestore.getInstance()
        val productsCollection = db.collection("products")

        // Fetch data from Firestore
        productsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val products = mutableListOf<Product>()
                for (document in querySnapshot) {
                    val firestoreProduct = document.toObject(FirestoreProduct::class.java)

                    val product = Product(
                        id = firestoreProduct.id,
                        title = firestoreProduct.title,
                        price = firestoreProduct.price,
                        description = firestoreProduct.description,
                        image = firestoreProduct.image,
                        location = firestoreProduct.location,
                        isPurchased = firestoreProduct.purchased
                    )

                    products.add(product)
                }
                if (products.isEmpty()) {
                    requireActivity().runOnUiThread {
                        UiUtility.showToast(requireActivity(), "No Items Added...")
                    }
                }
                requireActivity().runOnUiThread {
                    populateRecyclerView(products)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                requireActivity().runOnUiThread {
                    UiUtility.showToast(requireActivity(), "Couldn't load items.")
                }
            }
    }


    companion object {
        @JvmStatic
        fun newInstance() = ShopFragment()
    }

    private fun populateRecyclerView(products: List<Product>) {
        productRecyclerAdapter = ProductRecyclerAdapter(
            products,
            this,
            requireActivity().applicationContext
        )
        shopBinding.rvShop.adapter = productRecyclerAdapter
        shopBinding.rvShop.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onItemClicked(product: Product, position: Int) {
        val intent = Intent(requireActivity(), DetailViewActivity::class.java)
        intent.putExtra(AppConstant.KEY_PRODUCT, product)
        intent.putExtra(AppConstant.KEY_PRODUCT_POSITION, position)
        startDetailViewActivity.launch(intent)
    }
}
