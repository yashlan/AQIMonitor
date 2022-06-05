package com.c22_ce02.awmonitorapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.ListArticleAdapter
import com.c22_ce02.awmonitorapp.data.model.Article
import com.c22_ce02.awmonitorapp.databinding.FragmentArticleBinding
import com.c22_ce02.awmonitorapp.ui.activity.DetailArticleActivity
import com.c22_ce02.awmonitorapp.ui.view.model.ArticleViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.ArticleViewModelFactory
import com.c22_ce02.awmonitorapp.utils.convertToTimeAgo
import com.c22_ce02.awmonitorapp.utils.initializeTime4A
import com.c22_ce02.awmonitorapp.utils.setupAdapter
import org.jsoup.Jsoup
import timber.log.Timber

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val binding by viewBinding(FragmentArticleBinding::bind)
    private val articleViewModel: ArticleViewModel by viewModels {
        ArticleViewModelFactory()
    }
    private val listArticle = ArrayList<Article>()
    private lateinit var adapter: ListArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeTime4A()
        loadData()
    }

    private fun loadData(){
        showLoading(true)
        articleViewModel.listArticle.observe(requireActivity()){ list ->
            list?.forEach {
                val content = Jsoup.parse(it.content).text()
                val description = content.substring(0, content.length.coerceAtMost(200))
                listArticle.add(
                    Article(
                        imageurl = it.images?.get(0)?.url,
                        title = it.title,
                        description = "$description...",
                        created_by = getString(R.string.app_name),
                        created_at = convertToTimeAgo(it.published),
                        url = it.url
                    )
                )

                if(listArticle.size == list.size) {
                    setupAdapter(binding.recyclerviewArticle, true, addAdapterValue = {
                        binding.recyclerviewArticle.adapter = ListArticleAdapter(listArticle)
                    })
                    showLoading(false)
                }
            }
        }

        articleViewModel.errorMessage.observe(requireActivity()){
            if (it != null){
                if (BuildConfig.DEBUG) {
                    Timber.e(it)
                }
            }
        }

        articleViewModel.getArticle(BuildConfig.GOOGLE_API, true)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        const val URL_EXTRA = "URL_EXTRA"
    }
}