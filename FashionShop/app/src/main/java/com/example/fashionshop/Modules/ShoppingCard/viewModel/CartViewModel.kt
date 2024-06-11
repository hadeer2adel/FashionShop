package com.example.fashionshop.Modules.ShoppingCard.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fashionshop.Model.DraftOrderEditingQuntity
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.LineItem
import com.example.fashionshop.Model.TaxLineX
import com.example.fashionshop.Model.editOrderQuantityBody
import com.example.fashionshop.Repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel (private val repo: Repository
): ViewModel() {
    private  var _products: MutableLiveData<DraftOrders> = MutableLiveData<DraftOrders>()
    val products : LiveData<DraftOrders> = _products
    private var _products2: MutableLiveData<DraftOrders> =
        MutableLiveData<DraftOrders>()
    val products2: LiveData<DraftOrders> = _products2
    init {
        getAllDraftOrders()
    }



    fun getAllDraftOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "DraftOrders: ViewMOdel")
            val ProductList= repo.getDraftOrders()
            _products.postValue(ProductList)

        }
    }
    fun senddeleteDrafOrderRequest(id:Long
    )  {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "deleteSingleCustomerDrafOrder : ViewMOdel")
            repo.deleteSingleCustomerDrafOrder(id)
            getAllDraftOrders()
        }
    }

    fun sendeditChoosenQuantityRequest(
        id: Long,
        admin_graphql_api_id: String,
        applied_discount: Any?, // Ensure it's a Map
        custom: Boolean,
        fulfillment_service: String,
        gift_card: Boolean,
        grams: Int,
        lineItemId: Long,
        name: String,
        price: String,
        product_id: Any?,
        properties: List<Any>,
        quantity: Int,
        requires_shipping: Boolean,
        sku: Any?,
        tax_lines: List<TaxLineX>,
        taxable: Boolean,
        title: String,
        variant_id: String,
        variant_title: Any?,
        vendor: Any?
    ) {
        val lineItem = LineItem(
            admin_graphql_api_id = admin_graphql_api_id,
            applied_discount = applied_discount.toString(),
            custom = custom,
            fulfillment_service = fulfillment_service,
            gift_card = gift_card,
            grams = grams,
            id = lineItemId,
            name = name,
            price = price,
            product_id = product_id.toString(),
            properties = properties,
            quantity = quantity,
            requires_shipping = requires_shipping,
            sku = sku.toString(),
            tax_lines = tax_lines,
            taxable = taxable,
            title = title,
            variant_id = variant_id.toString(),
            variant_title = variant_title.toString(),
            vendor = vendor.toString()
        )
        val draftOrder = DraftOrderEditingQuntity(listOf(lineItem))
        val quantityRequest = editOrderQuantityBody(draftOrder)
        editSingleCustomerQuantityDraftOrder(id, quantityRequest)
    }


    fun editSingleCustomerQuantityDraftOrder(id: Long, quantitnyRequest: editOrderQuantityBody)
    {
        viewModelScope.launch(Dispatchers.IO) {

            val result = repo.editSingleCustomerAddressDraftOrderQuantity(id, quantitnyRequest)
            _products2.postValue(result)

        }
    }
}



