package com.example.newscrunch


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.newscrunch.adapter.HomeAdapter
import com.example.newscrunch.model.News
import com.example.newscrunch.util.ConnectionManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeActivity : AppCompatActivity() {

    //    recycler view declarations
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var homeAdapter: HomeAdapter
    lateinit var progressLayout: RelativeLayout

    //    this is the list of news objects that will be passed onto the adapter
    private val newsList = arrayListOf<News>()

    //    comparator to compare individual news objects based on the published date
    @RequiresApi(Build.VERSION_CODES.N)
    private val dateComparator = Comparator<News> { news1, news2 ->
        val comparator = Comparator.naturalOrder<LocalDateTime>()
        comparator.compare(news1.publishedAt, news2.publishedAt)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        initializing the variables
        homeAdapter = HomeAdapter(this@HomeActivity, newsList)
        layoutManager = LinearLayoutManager(this@HomeActivity)
        recyclerView = findViewById(R.id.recyclerHome)

        progressLayout = findViewById(R.id.progressLayout)


//        checks availability of internet connection
        if (ConnectionManager().checkConnection(this@HomeActivity)) {

            //        creates a request queue
            val queue = Volley.newRequestQueue(this@HomeActivity)
//        specifies the url for the api
            val url =
                "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"

//        a json object request to be added to the queue
            val jsonObjectRequest =
                @RequiresApi(Build.VERSION_CODES.O)
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
//                println("Response : $it")
                    if (it.getString("status").equals("ok")) {

                        val newsArray = it.getJSONArray("articles")
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

                        for (i in 0 until newsArray.length()) {
                            val newsObject = newsArray.getJSONObject(i)
                            val news = News(
                                newsObject.getJSONObject("source").getString("name"),
                                newsObject.getString("title"),
                                newsObject.getString("author"),
                                newsObject.getString("url"),
                                newsObject.getString("urlToImage"),
                                LocalDateTime.parse(newsObject.getString("publishedAt"), formatter)
                            )
                            newsList.add(news)
                        }

                        recyclerView.adapter = homeAdapter
                        recyclerView.layoutManager = layoutManager

//                    hiding progress layout
                        progressLayout.visibility = View.GONE

                    } else {
                        Toast.makeText(
                            this@HomeActivity,
                            "Some error occurred!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }, Response.ErrorListener {
                    println("Error: ${it.message}")
                    Toast.makeText(this@HomeActivity, "Some error occurred!", Toast.LENGTH_SHORT)
                        .show()
                }) {

                }

//        adding the request to the queue
            queue.add(jsonObjectRequest)

        } else {
//            display a alert dialog box
            val alert = AlertDialog.Builder(this@HomeActivity)
            alert.setTitle("Error!")
            alert.setMessage("No Internet Connection")
            alert.setPositiveButton("Open Settings") { text, listener ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                finish()
            }
            alert.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@HomeActivity)
            }
            alert.setOnDismissListener {
                ActivityCompat.finishAffinity(this@HomeActivity)
            }
            alert.create()
            alert.show()
        }

    }

    //    to add the menu item to sort the news
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_sort, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //    handles clicks on the menu items
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            click on oldest first
            R.id.sort_asc -> {
                Collections.sort(newsList, dateComparator)
            }

//            click on newest first
            R.id.sort_desc -> {
                Collections.sort(newsList, dateComparator)
                newsList.reverse()
            }
        }

//    notifies the adapter about the change in the order of news
        homeAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }
}

