package thisgeh.com.github.confereai.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface BrasilApiService {

    @GET("api/cnpj/v1/{cnpj}")
    suspend fun buscarCnpj(@Path("cnpj") cnpj: String): Response<EmpresaResponse>
}
