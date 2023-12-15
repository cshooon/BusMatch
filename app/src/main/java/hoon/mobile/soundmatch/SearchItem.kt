import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchItem(
    val routeId: String,
    val nodeId: String? = null,
    val sequence: Int? = null,
) : Parcelable
