package com.example.taskmovie.apis

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



    class ApiManager {

        companion object {
            private var retrofit: Retrofit? = null

            private fun getHttpClient(): OkHttpClient {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                return OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
            }

            private fun getRetrofit(): Retrofit {
                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/")
                        .client(getHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
                return retrofit!!
            }

            fun getWebService(): WebService {
                return getRetrofit().create(WebService::class.java)
            }
        }
    }




