import com.example.fashionshop.Model.*
import com.example.fashionshop.Repository.Repository
import kotlinx.coroutines.delay
import retrofit2.Response
import kotlin.random.Random

class FakeRepository : Repository {

    var shouldReturnError = false

    var customers = mutableListOf<CustomerResponse.Customer>()
    var draftOrders = mutableListOf<DraftOrderResponse>()


    override suspend fun getcustomers(id: Long): OneCustomer {
        TODO("Not yet implemented")
    }

    override suspend fun createCustomer(customerRequest: CustomerRequest): CustomerResponse {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        val customer = CustomerResponse.Customer(
            (customers.size + 1).toLong(),
            customerRequest.customer.first_name!!,
            customerRequest.customer.last_name!!,
            customerRequest.customer.email!!,
            "",
            "EGY",
            0L,
            0L
        )
        customers.add(customer)

        return CustomerResponse(customer)
    }

    override suspend fun getBrands(): Response<BrandResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getBrandProducts(vendor: String): Response<ProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getProducts(): Response<ProductResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun createDraftOrders(draftOrderResponse: DraftOrderResponse): DraftOrderResponse {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        val id = Random.nextLong(1L, 10000L)
        val fakeDraftOrderResponse = DraftOrderResponse(DraftOrderResponse.DraftOrder(id = id))
        return fakeDraftOrderResponse
    }

    override suspend fun updateDraftOrder(
        id: Long,
        draftOrder: DraftOrderResponse
    ): DraftOrderResponse {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        draftOrders.set((id - 1).toInt(), draftOrder)
        return draftOrders.get((id - 1).toInt())
    }

    override suspend fun getDraftOrder(id: Long): DraftOrderResponse {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        return draftOrders.get((id - 1).toInt())
    }

    override suspend fun updateCustomer(id: Long, updateCustomerRequest: UpdateCustomerRequest): CustomerResponse {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        var customer = customers.get(id.toInt() - 1)
        customer.note = updateCustomerRequest.customer.note!!
        customer.multipass_identifier = updateCustomerRequest.customer.note!!
        return CustomerResponse(customer)
    }

    override suspend fun getExchangeRates(
        apiKey: String,
        symbols: String,
        base: String
    ): ExchangeRatesResponseX {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override suspend fun createOrder(order: Map<String, OrderBody>): OrderBodyResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getSingleOrder(orderId: Long): Response<OrderResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomerByEmail(email: String): CustomerResponse {
        if (shouldReturnError) {
            throw Exception("Test exception")
        }
        customers.forEach{ customer ->  
            if (customer.email.equals(email)){
                return CustomerResponse(customers = listOf(customer))
            }
        }
        return CustomerResponse()
    }

    override suspend fun AddSingleCustomerAdreess(
        id: Long,
        addressRequest: AddressRequest
    ): AddressRequest {
        TODO("Not yet implemented")
    }

    override suspend fun editSingleCustomerAddress(
        customerID: Long,
        id: Long,
        addressRequest: AddressDefultRequest
    ): AddressUpdateRequest {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSingleCustomerAddress(customerID: Long, id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomerOrders(userId: Long): Response<OrderResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getDiscountCodes(): PriceRule {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(id: Long): ProductResponse {
        TODO("Not yet implemented")
    }
}
