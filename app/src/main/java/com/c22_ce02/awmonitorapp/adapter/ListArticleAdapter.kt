package com.c22_ce02.awmonitorapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.c22_ce02.awmonitorapp.data.model.Article
import com.c22_ce02.awmonitorapp.databinding.ItemRecycleviewArticleBinding
import com.c22_ce02.awmonitorapp.ui.activity.DetailArticleActivity
import com.c22_ce02.awmonitorapp.ui.fragment.ArticleFragment
import com.c22_ce02.awmonitorapp.utils.loadImageViaGlide

class ListArticleAdapter(
    private val listArticle: ArrayList<Article>
) : RecyclerView.Adapter<ListArticleAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder = ListViewHolder(
        ItemRecycleviewArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: ListViewHolder,
        position: Int
    ) = holder.bind(listArticle[position])

    override fun getItemCount(): Int = listArticle.size

    inner class ListViewHolder(private val binding: ItemRecycleviewArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            with(binding) {
                tvItemTitle.text = article.title
                tvItemDescription.text = article.description
                tvItemCreatedBy.text = article.created_by
                tvItemCreatedAt.text = article.created_at
                itemView.apply {
                    context.loadImageViaGlide(article.imageurl?.toUri(), binding.imgThumbnail)
                    setOnClickListener {
                        startAnimation(AlphaAnimation(1f, 0.5f))
                        val i = Intent(context, DetailArticleActivity::class.java)
                        i.putExtra(ArticleFragment.URL_EXTRA, article.url)
                        context.startActivity(i)
                    }
                }
            }
        }
    }
}