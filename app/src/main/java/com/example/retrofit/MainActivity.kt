package com.example.retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.AccessController.getContext
import java.util.*


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArticleAdapter
    private val articleList = mutableListOf<Articles>()
    private lateinit var actualCat: String
    private lateinit var actualCt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchNews.setOnQueryTextListener(this)
        initRecyclerView()
        actualCat = "general"
        actualCt = "us"
        binding.autoComplete.onItemClickListener = AdapterView.OnItemClickListener(){parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            searchNews(actualCat, item)

        }
        searchNews("general")
    }

    override fun onResume() {
        super.onResume()
        val countries = resources.getStringArray(R.array.countries)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, countries)
        binding.autoComplete.setAdapter(arrayAdapter)
    }


    private fun initRecyclerView(){
        adapter = ArticleAdapter(articleList)
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = adapter
    }

    private fun searchNews(category: String = "general", country: String = "us"){
        actualCat = category
        actualCt = country
        val api = Retrofit2()

        CoroutineScope(Dispatchers.IO).launch {
            val call = api.getService()?.getNewsByCategory(country = country, category = category, apiKey = "afc290ea74b74fb3b12b707670f51f51")
            val news: NewsResponse? = call?.body()

            runOnUiThread{
                if(call!!.isSuccessful){
                    if(news?.status.equals("ok")){
                        val articles = news?.articles ?: emptyList()
                        articleList.clear()
                        articleList.addAll(articles)
                        adapter.notifyDataSetChanged()
                    }
                    else{
                        showMessage("Error en webservices")
                    }
                }
                else{
                    showMessage("Error en retrofit")
                }
                hideKeyBoard()
            }
        }
    }

    private fun hideKeyBoard(){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showMessage(mensaje:String){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()){
            searchNews(query.toLowerCase(Locale.ROOT), actualCt)
        }

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}