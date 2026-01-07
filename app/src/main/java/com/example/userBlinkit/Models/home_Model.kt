import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class home_Model(
    val Image: Int,
    val Text: String
) : Parcelable
