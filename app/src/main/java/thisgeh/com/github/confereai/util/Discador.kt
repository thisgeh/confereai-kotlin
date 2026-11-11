package thisgeh.com.github.confereai.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast


fun abrirDiscador(context: Context, telefone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefone"))
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Este aparelho não consegue fazer ligações.", Toast.LENGTH_LONG).show()
    }
}
