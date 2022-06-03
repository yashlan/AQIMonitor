package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.ListArticleAdapter
import com.c22_ce02.awmonitorapp.data.model.Article
import com.c22_ce02.awmonitorapp.databinding.FragmentArticleBinding
import com.c22_ce02.awmonitorapp.ui.view.model.ArticleViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.ArticleViewModelFactory

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val binding by viewBinding(FragmentArticleBinding::bind)
    private val ArticleViewModel: ArticleViewModel by viewModels {
        ArticleViewModelFactory()
    }
    private val list = ArrayList<Article>()
    private lateinit var adapter: ListArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListArticleAdapter(list)

        showRecyclerList()
    }
    private fun showRecyclerList() {
        binding.recyclerviewArticle.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewArticle.setHasFixedSize(true)
        binding.recyclerviewArticle.adapter = adapter
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