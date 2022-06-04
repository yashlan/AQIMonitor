package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.BuildConfig
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.ListArticleAdapter
import com.c22_ce02.awmonitorapp.data.model.Article
import com.c22_ce02.awmonitorapp.data.response.ArticleResponse
import com.c22_ce02.awmonitorapp.databinding.FragmentArticleBinding
import com.c22_ce02.awmonitorapp.ui.view.model.ArticleViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.ArticleViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import timber.log.Timber

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val binding by viewBinding(FragmentArticleBinding::bind)
    private lateinit var dataArticle: ArticleResponse.ItemsItem
    private val ArticleViewModel: ArticleViewModel by viewModels {
        ArticleViewModelFactory()
    }
    private val listArticle = ArrayList<Article>()
    private lateinit var adapter: ListArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListArticleAdapter(listArticle)

        showRecyclerList()
    }
    private fun showRecyclerList() {
        binding.recyclerviewArticle.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewArticle.setHasFixedSize(true)
        binding.recyclerviewArticle.adapter = adapter
    }

    private fun loadData(){
        showLoading(true)
        ArticleViewModel.listArticle.observe(requireActivity()){
            if (it != null)
                dataArticle = it[0]
                getArticleData()
                showLoading(false)
        }
        ArticleViewModel.errorMessage.observe(requireActivity()){
            if (it != null)
                if (BuildConfig.DEBUG)
                    Timber.e(it)
        }
        ArticleViewModel.getArticle(BuildConfig.GOOGLE_API)
    }

    private fun getArticleData() {
        listArticle.add(
            Article(
                title = dataArticle.title,
                description = dataArticle.content,
                created_by = dataArticle.author.displayName,
                created_at = dataArticle.published
        )
        )
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
    companion object{
        private val TAG = ArticleFragment::class.java.simpleName
    }
}