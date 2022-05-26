package com.c22_ce02.awmonitorapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.c22_ce02.awmonitorapp.data.Article
import com.c22_ce02.awmonitorapp.databinding.ItemRowArticleBinding

class ListArticleAdapter(private val listArticle: ArrayList<Article>): RecyclerView.Adapter<ListArticleAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listArticle[position])
    }

    override fun getItemCount(): Int = listArticle.size

    class ListViewHolder (private var binding: ItemRowArticleBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article){
            with(binding){
                Glide.with(binding.imgItemPhoto)
                    .load(article.image)
                    .apply(RequestOptions().override(320,120))
                    .into(imgItemPhoto)
                tvItemTitle.text = article.title
                tvItemDescription.text = article.description
                tvItemCreatedBy.text = article.created_by
                tvItemCreatedAt.text = article.created_at
                itemView.setOnClickListener{
                    Toast.makeText(itemView.context,"you clicked item at : ${tvItemTitle}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: Article)
    }
}