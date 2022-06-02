package com.c22_ce02.awmonitorapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.c22_ce02.awmonitorapp.R
import com.c22_ce02.awmonitorapp.adapter.ListArticleAdapter
import com.c22_ce02.awmonitorapp.data.model.Article
import com.c22_ce02.awmonitorapp.databinding.FragmentArticleBinding

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val binding by viewBinding(FragmentArticleBinding::bind)
    private val list = ArrayList<Article>()
    private lateinit var adapter: ListArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListArticleAdapter(list)

        list.addAll(listArticle)
        showRecyclerList()
    }

    private val listArticle: ArrayList<Article>
        get() {
            val dataTitle = resources.getStringArray(R.array.title)
            val dataDescription = resources.getStringArray(R.array.description)
            val dataImage = resources.obtainTypedArray(R.array.image)
            val dataCreatedby = resources.getStringArray(R.array.created_by)
            val dataCreatedat = resources.getStringArray(R.array.created_at)
            val listArticle = ArrayList<Article>()
            for (i in dataTitle.indices) {
                val article = Article(
                    dataImage.getResourceId(i,-1),
                    dataTitle[i],
                    dataDescription[i],
                    dataCreatedby[i],
                    dataCreatedat[i],
                )
                listArticle.add(article)
            }
            return listArticle
        }

    private fun showRecyclerList() {
        binding.recyclerviewArticle.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewArticle.setHasFixedSize(true)
        binding.recyclerviewArticle.adapter = adapter
    }
}