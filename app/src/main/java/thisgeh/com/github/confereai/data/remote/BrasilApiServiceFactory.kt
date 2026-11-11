package thisgeh.com.github.confereai.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BrasilApiServiceFactory {

    fun create(): BrasilApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://brasilapi.com.br/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(BrasilApiService::class.java)
    }
}
