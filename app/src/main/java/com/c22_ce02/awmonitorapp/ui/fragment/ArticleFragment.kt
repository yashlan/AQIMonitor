package com.c22_ce02.awmonitorapp.ui.fragment

import android.content.Intent
import android.nfc.NfcAdapter.EXTRA_DATA
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
import com.c22_ce02.awmonitorapp.data.response.ArticleResponse.ItemsItem
import com.c22_ce02.awmonitorapp.databinding.FragmentArticleBinding
import com.c22_ce02.awmonitorapp.ui.activity.DetailArticleActivity
import com.c22_ce02.awmonitorapp.ui.view.model.ArticleViewModel
import com.c22_ce02.awmonitorapp.ui.view.modelfactory.ArticleViewModelFactory
import com.c22_ce02.awmonitorapp.utils.convertToTimeAgo
import com.c22_ce02.awmonitorapp.utils.initializeTime4A
import org.jsoup.Jsoup
import timber.log.Timber

@Suppress("DEPRECATION")
class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val binding by viewBinding(FragmentArticleBinding::bind)
    private lateinit var dataArticle: ItemsItem
    private val ArticleViewModel: ArticleViewModel by viewModels {
        ArticleViewModelFactory()
    }
    private val listArticle = ArrayList<Article>()
    private lateinit var adapter: ListArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeTime4A()
        adapter = ListArticleAdapter(listArticle)
        loadData()
    }
    private fun showRecyclerList() {
        binding.recyclerviewArticle.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewArticle.setHasFixedSize(true)
        binding.recyclerviewArticle.adapter = adapter
        adapter.setOnItemClickCallback(object : ListArticleAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Article) {
                showDetailArticle(data)
            }
        })
    }

    private fun showDetailArticle(data: Article) {
        val intent = Intent(activity, DetailArticleActivity::class.java)
        intent.putExtra(DetailArticleActivity.EXTRA_DATA, data)
        startActivity(intent)
    }

    private fun loadData(){
        showLoading(true)
        ArticleViewModel.listArticle.observe(requireActivity()){List->
            if (List != null) {
                List.forEach {
                    val content = Jsoup.parse(it.content).text()
                    val upTo200Characters = content.substring(0, content.length.coerceAtMost(200))
                    listArticle.add(
                        Article(
                            imageurl = it.images?.get(0)?.url,
                            title = it.title,
                            description = "$upTo200Characters...",
                            created_by = getString(R.string.app_name),
                            created_at = convertToTimeAgo(it.published),
                            url = it.url
                        )
                    )

                }
                showRecyclerList()
                showLoading(false)
            }
        }
        ArticleViewModel.errorMessage.observe(requireActivity()){
            if (it != null)
                if (BuildConfig.DEBUG)
                    Timber.e(it)
        }
        ArticleViewModel.getArticle(BuildConfig.GOOGLE_API, true)
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