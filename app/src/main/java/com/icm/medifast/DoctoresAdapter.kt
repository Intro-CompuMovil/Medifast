import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.icm.medifast.Doctores
import com.icm.medifast.R

class DoctoresAdapter(context: Context, resource: Int, objects: List<Doctores>) :
    ArrayAdapter<Doctores>(context, resource, objects) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.doctores_item, parent, false)

        val nombre = view.findViewById<TextView>(R.id.textView10)
        val especialidad = view.findViewById<TextView>(R.id.textView11)
        val foto = view.findViewById<ImageView>(R.id.doctorImageView)
        val currentDoctor = getItem(position)
        val photoResource = currentDoctor?.FotoResource ?: R.drawable.doctora1
        foto.setImageResource(photoResource)
        nombre.text = currentDoctor?.nombre
        especialidad.text = currentDoctor?.especialidad

        return view
    }
}
