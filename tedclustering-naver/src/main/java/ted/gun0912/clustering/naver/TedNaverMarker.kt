package ted.gun0912.clustering.naver

import android.content.Context
import android.graphics.Bitmap
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import ted.gun0912.clustering.TedMarker
import ted.gun0912.clustering.geometry.TedLatLng


class TedNaverMarker(val marker: Marker, val context: Context) : TedMarker<OverlayImage> {

    override fun setVisible(visible: Boolean) {
        marker.isVisible = visible
        var infoWindow = InfoWindow()
        infoWindow.adapter = object :InfoWindow.DefaultTextAdapter(context){
            override fun getText(p0: InfoWindow): CharSequence {
                return "테스트"
            }
        }
        infoWindow.open(marker)
    }

    override var position: TedLatLng
        get() = TedLatLng(marker.position.latitude, marker.position.longitude)
        set(value) {
            marker.position = LatLng(value.latitude, value.longitude)
        }

    override fun setImageDescriptor(imageDescriptor: OverlayImage) {
        marker.icon = imageDescriptor
    }

    override fun fromBitmap(bitmap: Bitmap): OverlayImage = OverlayImage.fromBitmap(bitmap)

}