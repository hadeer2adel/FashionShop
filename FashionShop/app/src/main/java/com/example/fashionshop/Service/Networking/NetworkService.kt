package com.example.fashionshop.Service.Networking

import com.example.fashionshop.Model.AddressDefultRequest
import com.example.fashionshop.Model.AddressRequest
import com.example.fashionshop.Model.AddressUpdateRequest
import com.example.fashionshop.Model.BrandResponse
import com.example.fashionshop.Model.CustomerRequest
import com.example.fashionshop.Model.CustomerResponse
import com.example.fashionshop.Model.DraftOrders
import com.example.fashionshop.Model.DraftOrderResponse
import com.example.fashionshop.Model.OneCustomer
import com.example.fashionshop.Model.OrderResponse
import com.example.fashionshop.Model.PriceRule
import com.example.fashionshop.Model.PriceRuleCount
import com.example.fashionshop.Model.ProductResponse
import com.example.fashionshop.Model.UpdateCustomerRequest
import com.example.fashionshop.Model.customers
import com.example.fashionshop.Model.editAddressBody
import com.example.fashionshop.Model.editOrderQuantityBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {
    @GET("customers.json")
    suspend fun getcustomers(): customers

    @POST("customers.json")
    suspend fun createCustomer(
        @Body customer: CustomerRequest
    ): CustomerResponse

    @GET("smart_collections.json")
    suspend fun getBrands(): Response<BrandResponse>

    @GET("products.json")
    suspend fun getBrandProducts(@Query("vendor") vendor: String): Response<ProductResponse>
    @GET("products.json")
    suspend fun getProducts(): Response<ProductResponse>
    @GET("customers/search.json")
    suspend fun getCustomerByEmail(
        @Query("email") email: String
    ): customers

    @GET("customers/7371713577180.json")
    suspend fun getSingleCustomer(): OneCustomer

    @POST("customers/7371713577180/addresses.json")
    suspend fun AddSingleCustomerAdreess(  @Body addressRequest: AddressRequest): AddressRequest


    @PUT("customers/7371713577180/addresses/{id}.json")
    suspend fun editSingleCustomerAddress(
        @Path("id") id: Long,
        @Body addressRequest: AddressDefultRequest
    ): AddressUpdateRequest


    @DELETE("customers/7371713577180/addresses/{id}.json")
    suspend fun deleteSingleCustomerAddress(
        @Path("id") id: Long,
    )


    @GET("draft_orders.json")
    suspend fun getDraftOrders(): DraftOrders


    @DELETE("draft_orders/{id}.json")
    suspend fun deleteSingleCustomerDrafOrder(
        @Path("id") id: Long,
    )

    @PUT("draft_orders/{id}.json")
    suspend fun editSingleCustomerAddressDraftOrder(
        @Path("id") id: Long,
        @Body addressRequest: editAddressBody
    ): DraftOrders
    @PUT("draft_orders/{id}.json")
    suspend fun editSingleCustomerAddressDraftOrderQuantity(
        @Path("id") id: Long,
        @Body QuantityRequest: editOrderQuantityBody
    ): DraftOrders

    @GET("customers/{id}/orders.json")
    suspend fun getCustomerOrders(@Path("id") userId: Long): Response<OrderResponse>


    @GET("price_rules.json")
    suspend fun getDiscountCodes(): PriceRule
    @GET("products/{id}.json")
    suspend fun getProductById(@Path("id") id: Long): ProductResponse


    @GET("price_rules/count.json")
    suspend fun getDiscountCodesCount(): PriceRuleCount
    @POST("draft_orders.json")
    suspend fun createDraftOrders(
        @Body draftOrder: DraftOrderResponse
    ): DraftOrderResponse

    @PUT("draft_orders/{id}.json")
    suspend fun updateDraftOrder(
        @Path("id") id: Long,
        @Body draftOrder: DraftOrderResponse
    ): DraftOrderResponse

    @GET("draft_orders/{id}.json")
    suspend fun getDraftOrder(@Path("id") id: Long): DraftOrderResponse

    @PUT("customers/{id}.json")
    suspend fun updateCustomer(
        @Path("id") id: Long,
        @Body customer: UpdateCustomerRequest
    ): CustomerResponse

}



