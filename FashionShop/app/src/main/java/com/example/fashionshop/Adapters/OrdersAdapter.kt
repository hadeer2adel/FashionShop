package com.example.fashionshop.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionshop.Model.Order
import com.example.fashionshop.databinding.OrderCardBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersAdapter(private val context: Context,
                    private val onCardClick: (order : Order)->Unit)
    : ListAdapter<Order , OrdersAdapter.OrderViewHolder>(OrderDiffUtil()){

    lateinit var binding: OrderCardBinding
    class OrderViewHolder (var binding: OrderCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersAdapter.OrderViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = OrderCardBinding.inflate(inflater, parent, false)
        return OrdersAdapter.OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdersAdapter.OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.binding.apply {
            tvOrderNumber.text = order.order_number.toString()
            tvOrderPrice.text =  order.current_total_price
            tvProductsNumber.text = (order.line_items?.size).toString()
            tvDate.text = formatDate(order.created_at.toString())
            card.setOnClickListener { onCardClick(order) }
        }
    }


}
class OrderDiffUtil : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}

fun formatDate(date: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate: Date = format.parse(date) as Date
    val stringFormat = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
    return stringFormat.format(formattedDate)
}


