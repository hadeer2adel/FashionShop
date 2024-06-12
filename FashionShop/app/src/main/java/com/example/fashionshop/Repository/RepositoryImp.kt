package com.example.fashionshop.Repository
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.customers
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.DraftOrdersRequest
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleCount
import com.example.fashionshop.Model.Product
import com.example.fashionshop.Model.UpdateCustomerRequest
import com.example.fashionshop.Model.editAddressBody
import com.example.fashionshop.Model.editOrderQuantityBody
import com.example.fashionshop.Service.Networking.NetworkManager
import retrofit2.Response

class RepositoryImp constructor(
    private var networkManager: NetworkManager
): Repository {

    companion object {
        private var instance: RepositoryImp? = null
        fun getInstance(productNetworkManager: NetworkManager)
                : RepositoryImp {
            return instance ?: synchronized(this) {
                val temp = RepositoryImp(productNetworkManager)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getcustomers(): OneCustomer {
        return networkManager.getcutomers()

    }

    override suspend fun createCustomer(
        customer: CustomerRequest
    ): CustomerResponse {
        return networkManager.createCustomer(customer)
    }

    override suspend fun getBrands(): Response<BrandResponse> {
        return networkManager.getBrands()
    }

    override suspend fun getBrandProducts(vendor: String): Response<ProductResponse> {
        return networkManager.getBrandProducts(vendor)
    }

    override suspend fun getProducts(): Response<ProductResponse> {
        return networkManager.getProducts()
    }


    override suspend fun getCustomerByEmail(email: String): customers {
        return networkManager.getCustomerByEmail(email)
    }

    override suspend fun AddSingleCustomerAdreess(addressRequest: AddressRequest): AddressRequest {
        return networkManager.AddSingleCustomerAdreess(addressRequest)
    }

    override suspend fun editSingleCustomerAddress(
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest {
        return networkManager.editSingleCustomerAddress(id, addressRequest)
    }

    override suspend fun deleteSingleCustomerAddress(id: Long) {
        return networkManager.deleteSingleCustomerAddress(id)
    }

    override suspend fun getDraftOrders(): DraftOrders {
        return networkManager.getDraftOrders()
    }

    override suspend fun deleteSingleCustomerDrafOrder(id: Long) {
        return networkManager.deleteSingleCustomerDrafOrder(id)
    }

    override suspend fun editSingleCustomerAddressDraftOrder(
        id: Long,
        addressRequest: editAddressBody
    ): DraftOrders {
        return networkManager.editSingleCustomerAddressDraftOrder(id, addressRequest)
    }

    override suspend fun editSingleCustomerAddressDraftOrderQuantity(
        id: Long,
        quantityRequest: editOrderQuantityBody
    ): DraftOrders {
        return networkManager.editSingleCustomerAddressDraftOrderQuantity(id, quantityRequest)

    }


    override suspend fun getDiscountCodesCount(): PriceRuleCount {
        return networkManager.getDiscountCodesCount()

    }

    override suspend fun getDiscountCodes(): PriceRule {
        return networkManager.getDiscountCodes()

    }
    override suspend fun getProductById(id: Long): ProductResponse {
        return networkManager.getProductById(id)
    }

    override suspend fun createDraftOrders(draftOrders: DraftOrdersRequest): DraftOrderResponse {
        return networkManager.createDraftOrders(draftOrders)
    }

    override suspend fun updateDraftOrder(id: Long): DraftOrderResponse {
        return networkManager.updateDraftOrder(id)
    }

    override suspend fun getDraftOrder(id: Long): DraftOrderResponse {
        return networkManager.getDraftOrder(id)
    }

    override suspend fun updateCustomer(
        id: Long,
        customer: UpdateCustomerRequest
    ): CustomerResponse {
        return networkManager.updateCustomer(id, customer)
    }
}
