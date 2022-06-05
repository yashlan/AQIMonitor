package com.c22_ce02.awmonitorapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.c22_ce02.awmonitorapp.data.model.Article
import com.c22_ce02.awmonitorapp.databinding.ItemRowArticleBinding
import com.c22_ce02.awmonitorapp.utils.loadImageViaGlide

class ListArticleAdapter(private val listArticle: ArrayList<Article>) : RecyclerView.Adapter<ListArticleAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listArticle[position])
    }

    override fun getItemCount(): Int = listArticle.size

    inner class ListViewHolder(private var binding: ItemRowArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            with(binding) {
                tvItemTitle.text = article.title
                tvItemDescription.text = article.description
                tvItemCreatedBy.text = article.created_by
                tvItemCreatedAt.text = article.created_at
                itemView.apply {
                    context.loadImageViaGlide(article.imageurl?.toUri(),binding.imgItemPhoto)
                    setOnClickListener{
                        onItemClickCallback.onItemClicked(article)
                    }
                }
                }
            }
        }
    interface OnItemClickCallback {
        fun onItemClicked(data: Article)
    }
}