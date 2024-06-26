package com.example.fashionshop.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Model.Addresse
import com.example.fashionshop.Modules.Address.view.AddressListener
import com.example.fashionshop.R

class AddressAdapter(private val listener: AddressListener ,private val showDeleteIcon: Boolean) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private var addressList: List<Addresse> = emptyList()
    private var selectedItemPosition = -1

    fun setAddressList(addressList: List<Addresse>) {
        this.addressList = addressList
//        notifyDataSetChanged() // Notify adapter that data set has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.address_card, parent, false)
        return AddressViewHolder(itemView)
    }

    fun getAddressList(): List<Addresse> {
        return addressList
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val currentAddress = addressList[position]
        holder.countryTextView.text = currentAddress.country
        holder.addressTextView.text = currentAddress.address1
        holder.addressTextView2.text = if (currentAddress.address2 == null ) currentAddress.address1 else "${currentAddress.address2}"
       // holder.addressTextView2.text = "${currentAddress.address2}" if (currentAddress.address2 == "null")
        holder.phoneTextView.text = currentAddress.phone
        holder.itemView.isSelected = selectedItemPosition == position

        if (currentAddress.default) {
            holder.cardView.setCardBackgroundColor(holder.itemView.context.getColor(R.color.default_address_color))
            holder.deleteIcon.visibility = View.GONE
        } else {
            holder.cardView.setCardBackgroundColor(holder.itemView.context.getColor(R.color.non_default_address_color))
            holder.deleteIcon.visibility = if (showDeleteIcon) View.VISIBLE else View.GONE

        }

        holder.itemView.setOnLongClickListener {
            selectedItemPosition = holder.adapterPosition
            listener.setAddressDefault(currentAddress.id, true)
            notifyDataSetChanged() // Notify adapter that data set has changed
            true // Consume the long click
        }
       // holder.deleteIcon.visibility = if (showDeleteIcon) View.VISIBLE else View.GONE

        holder.deleteIcon.setOnClickListener {
            listener.deleteAddress(currentAddress.id)
        }
    }

    override fun getItemCount() = addressList.size

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryTextView: TextView = itemView.findViewById(R.id.TextCountry)
        val addressTextView: TextView = itemView.findViewById(R.id.TextAddress)
        val addressTextView2: TextView = itemView.findViewById(R.id.TextAddress2)
        val phoneTextView: TextView = itemView.findViewById(R.id.TextPhone)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
    }
}