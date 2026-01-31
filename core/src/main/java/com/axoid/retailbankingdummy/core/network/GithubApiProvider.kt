package com.axoid.retailbankingdummy.core.network

// In :core:network/src/main/java/com/axoid/retailbankingdummy/core/network/GithubApiProvider.kt


import com.axoid.retailbankingdummy.core.model.Account
import com.google.gson.Gson
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.math.BigDecimal

object GithubApiProvider {

    private const val GITHUB_HOSTNAME = "api.github.com"

    // These are the public key hashes for api.github.com's SSL certificates.
    // If any of these don't match, the connection will be rejected.
    private val certificatePinner = CertificatePinner.Builder()
        .add(GITHUB_HOSTNAME, "sha256/H8zmHRgw4cFDQn+MvcyfhImeWNY4kN9HXO/J9xX32gk=")
        .add(GITHUB_HOSTNAME, "sha256/ZSagvDzjltLkewXEBuDxIzpW/dpVw1Juvvmd0hhkzdY=")
        .add(GITHUB_HOSTNAME, "sha256/sLVjNUaFYfW7n6EtgBeEpjOlcnBdNPMrZDRF36iwBdE=")
        .build()

    // START: New Mock Data Interceptor
    private val mockAccountsInterceptor = Interceptor { chain ->
        val request = chain.request()
        if (request.url.encodedPath.endsWith("/user/accounts")) {
            // If the request is for our mock endpoint, intercept it
            val mockAccounts = listOf(
                Account("1", "Checking", BigDecimal("15430.25"), "...1234"),
                Account("2", "Savings", BigDecimal("85600.50"), "...5678"),
                Account("3", "Credit Card", BigDecimal("-950.75"), "...4321")
            )
            val json = Gson().toJson(mockAccounts)
            val responseBody = json.toResponseBody("application/json".toMediaType())

            // Build and return a successful response with the mock data
            Response.Builder()
                .code(200)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .body(responseBody)
                .build()
        } else {
            // For all other requests (like the real GitHub API), proceed as normal
            chain.proceed(request)
        }
    }
    private val okHttpClient = OkHttpClient.Builder()
        // Attach the CertificatePinner
        .certificatePinner(certificatePinner)
        .addInterceptor(mockAccountsInterceptor)
        // Add a logging interceptor to see network traffic in Logcat
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://$GITHUB_HOSTNAME/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Create a public API service instance
    val githubApiService: GithubApiService = retrofit.create(GithubApiService::class.java)
}

/**
 * A simple Retrofit service interface to test against the GitHub API.
 */
interface GithubApiService {
    @GET("users/{user}")
    suspend fun getUser(@Path("user") user: String): GithubUser

    // Add this mock endpoint. The interceptor will handle it.
    @GET("user/accounts")
    suspend fun getAccounts(): List<Account>
}

/**
 * A simple data class to hold the response from the GitHub API.
 */
data class GithubUser(
    val login: String,
    val id: Long,
    val name: String?,
    val company: String?
)
