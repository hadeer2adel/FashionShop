package com.example.fashionshop.Repository
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.CheckoutSessionResponse
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.ExchangeRatesResponseX
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.OrderBody
import com.example.fashionshop.Model.OrderBodyResponse
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.UpdateCustomerRequest
import com.example.fashionshop.Service.Networking.NetworkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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


    override suspend fun getcustomers(id:Long): OneCustomer {
        return networkManager.getcutomers(id)
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


    override suspend fun getCustomerByEmail(email: String): CustomerResponse {
        return networkManager.getCustomerByEmail(email)
    }


    override suspend fun AddSingleCustomerAdreess(id:Long,addressRequest: AddressRequest): AddressRequest {
        return networkManager.AddSingleCustomerAdreess(id,addressRequest)
    }

    override suspend fun editSingleCustomerAddress(
        customerID:Long,
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest {
        return networkManager.editSingleCustomerAddress(customerID,id, addressRequest)
    }

    override suspend fun deleteSingleCustomerAddress(customerID: Long,id: Long) {
        return networkManager.deleteSingleCustomerAddress(customerID,id)
    }

    override suspend fun getCustomerOrders(userId: Long): Response<OrderResponse> {
        return  networkManager.getCustomerOrders(userId)
    }

    override suspend fun getDiscountCodes(): Flow<PriceRule> {
        return flowOf(networkManager.getDiscountCodes())

    }
    override suspend fun getProductById(id: Long): ProductResponse {
        return networkManager.getProductById(id)
    }

    override suspend fun createDraftOrders(draftOrder: DraftOrderResponse): DraftOrderResponse {
        return networkManager.createDraftOrders(draftOrder)
    }

    override suspend fun updateDraftOrder(id: Long, draftOrder: DraftOrderResponse): DraftOrderResponse{
        return networkManager.updateDraftOrder(id, draftOrder)
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

    override suspend fun createOrder(order:  Map<String, OrderBody>): OrderBodyResponse {
        return networkManager.createOrder(order)
    }

    override suspend fun getSingleOrder(orderId: Long): Response<OrderResponse> {
        return networkManager.getSingleOrder(orderId)
    }


    override suspend fun getExchangeRates(apiKey: String,symbols :String, base: String): ExchangeRatesResponseX{
        return networkManager.getExchangeRates(apiKey,symbols,base)
    }

    override suspend fun createCheckoutSession(
        successUrl: String,
        cancelUrl: String,
        customerEmail: String,
        currency: String,
        productName: String,
        productDescription: String,
        unitAmountDecimal: Int,
        quantity: Int,
        mode: String,
        paymentMethodType: String
    ): CheckoutSessionResponse {
     return   networkManager.createCheckoutSession(successUrl,cancelUrl,customerEmail,currency,productName,productDescription,unitAmountDecimal,quantity,mode,paymentMethodType)
    }


}
