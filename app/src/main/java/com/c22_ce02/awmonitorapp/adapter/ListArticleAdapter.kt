package com.c22_ce02.awmonitorapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.c22_ce02.awmonitorapp.data.model.Article
import com.c22_ce02.awmonitorapp.databinding.ItemRowArticleBinding

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
                Glide.with(binding.imgItemPhoto)
                    .load(article.image)
                    .apply(RequestOptions().override(320, 120))
                    .into(imgItemPhoto)
                binding.tvItemTitle.text = article.title
                binding.tvItemDescription.text = article.description
                binding.tvItemCreatedBy.text = article.created_by
                binding.tvItemCreatedAt.text = article.created_at
                itemView.apply {
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