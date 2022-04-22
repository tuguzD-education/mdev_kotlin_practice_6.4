package io.github.tuguzd.musicclient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.tuguzd.musicclient.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    companion object {
        private const val HEROKU_URL: String =
            //"http://10.0.2.2:5000/"
            "https://mdev-kotlin-crud.herokuapp.com"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit: Retrofit = Retrofit.Builder().baseUrl(HEROKU_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service: MusicController = retrofit.create(MusicController::class.java)

        binding.create.setOnClickListener {
            service.create(MusicEntry(
                if (binding.id.text.toString().isBlank()) 0
                    else binding.id.text.toString().toInt(),
                binding.name.text.toString(),
                binding.album.text.toString())
            ).enqueue(callback<Int> {
                binding.result.text = it.toString()
            })
        }

        binding.readEntry.setOnClickListener {
            service.readEntry(
                if (binding.id.text.toString().isBlank()) 0
                    else binding.id.text.toString().toInt()
            ).enqueue(callback<MusicEntry> {
                println(it)
                binding.result.text =
                    it?.toString() ?:
                    "There isn't music with such ID!"
            })
        }

        binding.update.setOnClickListener {
            service.update(
                MusicEntry(
                    if (binding.id.text.toString().isBlank()) 0
                        else binding.id.text.toString().toInt(),
                    binding.name.text.toString(),
                    binding.album.text.toString())
            ).enqueue(callback<Boolean> {
                binding.result.text = it.toString()
            })
        }

        binding.delete.setOnClickListener {
            service.delete(
                if (binding.id.text.toString().isBlank()) 0
                    else binding.id.text.toString().toInt(),
            ).enqueue(callback<Boolean> {
                binding.result.text = it.toString()
            })
        }

        binding.read.setOnClickListener {
            service.read().enqueue(callback<List<MusicEntry>> { list ->
                binding.result.text =
                    if (list?.isNotEmpty() == true)
                        list.joinToString(separator = "\n") { "$it" }
                    else "List of music is empty!"
            })
        }
    }

    private fun <T> callback(onSuccess: (response: T?) -> Unit) =
        object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                onSuccess(null)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                onSuccess(response.body())
            }
    }
}
