package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.ListArticleAdapter
import com.c22_ce02.awmonitorapp.data.model.AirQualityMaps
import com.c22_ce02.awmonitorapp.data.model.Article
import com.c22_ce02.awmonitorapp.data.response.ArticleResponse
import com.c22_ce02.awmonitorapp.data.response.CurrentAirQuality34ProvinceResponse
import com.c22_ce02.awmonitorapp.databinding.FragmentArticleBinding
import com.c22_ce02.awmonitorapp.ui.view.model.ArticleViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.ArticleViewModelFactory
import com.c22_ce02.awmonitorapp.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import timber.log.Timber


class ArticleFragment : Fragment(R.layout.fragment_article) {

    private var callApiHandler: Handler? = null
    private val binding by viewBinding(FragmentArticleBinding::bind, onViewDestroyed = {
        callApiHandler?.removeCallbacksAndMessages(null)
    })
    private val articleViewModel: ArticleViewModel by viewModels {
        ArticleViewModelFactory()
    }
    private lateinit var listArticle: ArrayList<Article>


    override fun onDestroy() {
        super.onDestroy()
        callApiHandler?.removeCallbacksAndMessages(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(activity != null) {
            binding.swipeRefresh.setOnRefreshListener {
                refreshFragment()
            }

/*        if (!isNetworkAvailable(requireContext(), showNotAvailableInfo = true)) {
            binding.shimmerFragmentArticle.hideShimmer()
            return
        }*/

            initializeTime4A()

            callApiHandler = Handler(Looper.getMainLooper())
            callApiHandler?.postDelayed({
                getListArticle()
            }, DELAY_CALL_API)
        }

    }

    private fun refreshFragment() {
        val refreshFragmentHandler = Handler(Looper.getMainLooper())
        refreshFragmentHandler.postDelayed({
            if (view == null) return@postDelayed
            binding.swipeRefresh.isRefreshing = false
            if (!binding.swipeRefresh.isRefreshing) {
                val navHostFragment =
                    requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home_activity)
                navHostFragment?.findNavController()?.navigate(R.id.navigation_article)
            }
        }, DELAY_REFRESH)
    }

    private fun getListArticle() {
        val json =
            resources.openRawResource(R.raw.response_blogger).bufferedReader().use { it.readText() }
        getListDataLocal(json)
    }

    private fun getListDataLocal(json: String) {
        try {
            listArticle = ArrayList()
            val jsonObject = JSONObject(json)
            val items = jsonObject.getJSONArray("items")
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val imageUrl = item.getJSONArray("images").getJSONObject(0).getString("url")
                val title = item.getString("title")
                val content = Jsoup.parse(item.getString("content")).text()
                val description =
                    content.substring(0, content.length.coerceAtMost(MAX_CHAR))
                val createdAt = item.getString("published")
                val url = item.getString("url")
                listArticle.add(
                    Article(
                        imageUrl = imageUrl,
                        title = title,
                        description = "$description...",
                        created_by = getString(R.string.app_name),
                        created_at = convertToTimeAgo(createdAt),
                        url = url
                    )
                )
            }

            setupAdapter(binding.recyclerviewArticle, true, addAdapterValue = {
                binding.recyclerviewArticle.adapter =
                    ListArticleAdapter(listArticle)
            })
            showLoading(false)
            binding.root.setBackgroundColor(
                ActivityCompat.getColor(
                    requireContext(),
                    R.color.bg_fragment_article_color
                )
            )

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun loadData() {
        showLoading(true)
        articleViewModel.getArticle(
            onSuccess = { list ->
                if (list != null && list.isNotEmpty()) {
                    listArticle = ArrayList()
                    listArticle.clear()
                    list.forEach {
                        val content = Jsoup.parse(it.content).text()
                        val description =
                            content.substring(0, content.length.coerceAtMost(MAX_CHAR))
                        listArticle.add(
                            Article(
                                imageUrl = it.images?.get(0)?.url,
                                title = it.title,
                                description = "$description...",
                                created_by = getString(R.string.app_name),
                                created_at = convertToTimeAgo(it.published),
                                url = it.url
                            )
                        )

                        if (listArticle.size == list.size) {
                            if (view == null) return@forEach
                            setupAdapter(binding.recyclerviewArticle, true, addAdapterValue = {
                                binding.recyclerviewArticle.adapter =
                                    ListArticleAdapter(listArticle)
                            })
                            showLoading(false)
                            binding.root.setBackgroundColor(
                                ActivityCompat.getColor(
                                    requireContext(),
                                    R.color.bg_fragment_article_color
                                )
                            )
                        }
                    }
                }
            },
            onError = { errorMsg ->
                showLoading(false)
                if (errorMsg != null) {
                    showToast(errorMsg)
                }
            }
        )
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerFragmentArticle.visibility = View.VISIBLE
            binding.shimmerFragmentArticle.startShimmer()
            binding.recyclerviewArticle.visibility = View.GONE
        } else {
            binding.shimmerFragmentArticle.stopShimmer()
            binding.shimmerFragmentArticle.visibility = View.GONE
            binding.recyclerviewArticle.visibility = View.VISIBLE
        }
    }

    companion object {
        const val URL_EXTRA = "URL_EXTRA"
        private const val MAX_CHAR = 120
        private const val DELAY_REFRESH: Long = 500
        private const val DELAY_CALL_API: Long = 2000
    }
}