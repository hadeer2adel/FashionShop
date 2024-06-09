package com.example.fashionshop.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.R

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private var addressList: List<Addresse> = emptyList()

    fun setAddressList(addressList: List<Addresse>) {
        this.addressList = addressList
        notifyDataSetChanged() // Notify adapter that data set has changed
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.address_card, parent, false)
        return AddressViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val currentAddress = addressList[position]
        holder.countryTextView.text = "Country: ${currentAddress.country}"
        holder.addressTextView.text = "Address 1: ${currentAddress.address1}"
        holder.addressTextView2.text = "Address 2: ${currentAddress.address2}"
        holder.phoneTextView.text = "Phone: ${currentAddress.phone}"
    }

    override fun getItemCount() = addressList.size

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryTextView: TextView = itemView.findViewById(R.id.TextCountry)
        val addressTextView: TextView = itemView.findViewById(R.id.TextAddress)
        val addressTextView2: TextView = itemView.findViewById(R.id.TextAddress2)
        val phoneTextView: TextView = itemView.findViewById(R.id.TextPhone)
    }
}
