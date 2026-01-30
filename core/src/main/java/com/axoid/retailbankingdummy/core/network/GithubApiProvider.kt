package com.axoid.retailbankingdummy.core.network

// In :core:network/src/main/java/com/axoid/retailbankingdummy/core/network/GithubApiProvider.kt



import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object GithubApiProvider {

    private const val GITHUB_HOSTNAME = "api.github.com"

    // These are the public key hashes for api.github.com's SSL certificates.
    // If any of these don't match, the connection will be rejected.
    private val certificatePinner = CertificatePinner.Builder()
        .add(GITHUB_HOSTNAME, "sha256/H8zmHRgw4cFDQn+MvcyfhImeWNY4kN9HXO/J9xX32gk=")
        .add(GITHUB_HOSTNAME, "sha256/ZSagvDzjltLkewXEBuDxIzpW/dpVw1Juvvmd0hhkzdY=")
        .add(GITHUB_HOSTNAME, "sha256/sLVjNUaFYfW7n6EtgBeEpjOlcnBdNPMrZDRF36iwBdE=")
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        // Attach the CertificatePinner
        .certificatePinner(certificatePinner)
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
