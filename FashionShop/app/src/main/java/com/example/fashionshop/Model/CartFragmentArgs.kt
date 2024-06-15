import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartFragmentArgs(val draftOrderIds: List<Long>) : Parcelable {

    companion object {
        fun Bundle.toCartFragmentArgs(): CartFragmentArgs {
            val draftOrderIds = this.getLongArray("draftOrderIds")?.toList() ?: emptyList()
            return CartFragmentArgs(draftOrderIds)
        }
    }

    fun toBundle(): Bundle {
        return Bundle().apply {
            putLongArray("draftOrderIds", draftOrderIds.toLongArray())
        }
    }
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}


@Parcelize
data class ChooseAddressFragmentArgs(val draftOrderIds: List<Long>) : Parcelable {

    companion object {
        fun fromBundle(bundle: Bundle): ChooseAddressFragmentArgs {
            return ChooseAddressFragmentArgs(bundle.getLongArray("draftOrderIds")?.toList() ?: emptyList())
        }
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}



