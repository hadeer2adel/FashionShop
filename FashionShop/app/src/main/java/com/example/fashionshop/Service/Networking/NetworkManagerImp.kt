package com.example.fashionshop.Service.Networking
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.ProductResponse
import retrofit2.Response
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


class NetworkManagerImp private constructor(): NetworkManager {
    private val networkService : NetworkService by lazy {
        RetrofitHelper.retrofitInstance.create(NetworkService::class.java)
    }
    private val networkServiceExchanges:networkService2 by lazy {
        RetrofitHelperExchanges.exchangeRatesRetrofit.create(networkService2::class.java)
    }
    private val networkServicePayment:NetworkService by lazy {
        RetrofitHelperPayment.retrofitInstance.create(NetworkService::class.java)
    }

    companion object{

        private var instance: NetworkManagerImp?=null
        fun getInstance(): NetworkManagerImp {
            return instance ?: synchronized(this){
                val temp= NetworkManagerImp()
                instance =temp
                temp
            }
        }
    }

    override suspend fun getcutomers( id: Long): OneCustomer {
        val responce= networkService.getSingleCustomer(id)
        return responce
    }

    override suspend fun AddSingleCustomerAdreess( id: Long,addressRequest: AddressRequest): AddressRequest {
        val responce= networkService.AddSingleCustomerAdreess(id,addressRequest)
        return responce    }

    override suspend fun editSingleCustomerAddress(
        customerId:Long,
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest {
        val responce= networkService.editSingleCustomerAddress(customerId,id,addressRequest)
        return responce    }

    override suspend fun deleteSingleCustomerAddress(customerId:Long,id: Long) {
        networkService.deleteSingleCustomerAddress(customerId,id)
    }

    override suspend fun createCustomer(
        customer: CustomerRequest
    ): CustomerResponse {
        return networkService.createCustomer(customer)
    }

    override suspend fun getBrands(): BrandResponse {
        return networkService.getBrands()
    }

    override suspend fun getProducts(): ProductResponse {
        return networkService.getProducts()
    }

    override suspend fun getBrandProducts(vendor: String): ProductResponse {
        return  networkService.getBrandProducts(vendor)
    }


    override suspend fun getCustomerByEmail(email: String): CustomerResponse {
        return networkService.getCustomerByEmail(email)
    }

    override suspend fun getCustomerOrders(userId: Long): OrderResponse {
        return networkService.getCustomerOrders(userId)
    }

    override suspend fun getDiscountCodes(): PriceRule {
        return networkService.getDiscountCodes()
        }
    override suspend fun getProductById(id: Long): ProductResponse {
        return networkService.getProductById(id)
    }

    override suspend fun createDraftOrders(draftOrder: DraftOrderResponse): DraftOrderResponse {
        return networkService.createDraftOrders(draftOrder)
    }

    override suspend fun updateDraftOrder(id: Long, draftOrder: DraftOrderResponse): DraftOrderResponse{
        return networkService.updateDraftOrder(id, draftOrder)
    }

    override suspend fun getDraftOrder(id: Long): DraftOrderResponse {
        return networkService.getDraftOrder(id)
    }

    override suspend fun updateCustomer(
        id: Long,
        customer: UpdateCustomerRequest
    ): CustomerResponse {
        return networkService.updateCustomer(id, customer)
    }
    override suspend fun createOrder(order:  Map<String, OrderBody>): OrderBodyResponse {
        return networkService.createOrder(order)
    }

    override suspend fun getSingleOrder(orderId: Long): OrderResponse {
        return networkService.getSingleOrder(orderId)
    }
    override suspend fun getExchangeRates(apiKey: String,symbols :String, base: String): ExchangeRatesResponseX{
        return networkServiceExchanges.getExchangeRates(apiKey,symbols,base)
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
      return   networkServicePayment.createCheckoutSession(successUrl,cancelUrl,customerEmail,currency,productName,productDescription,unitAmountDecimal,quantity,mode,paymentMethodType)
    }


}