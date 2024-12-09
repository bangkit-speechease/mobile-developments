package com.example.speechease.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.speechease.data.pref.UserPreference
import com.example.speechease.data.response.ContentData
import com.example.speechease.databinding.ItemListBinding
import com.example.speechease.ui.practicedetail.PracticeDetailActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class PracticeAdapter(private val userPreference: UserPreference) : ListAdapter<ContentData, PracticeAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(content: ContentData, userPreference: UserPreference) {
            Glide.with(binding.imgPractice.context)
                .load(content.imageUrl)
                .into(binding.imgPractice)
            binding.tvPractice.text = content.title

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, PracticeDetailActivity::class.java).apply {
                    putExtra("CONTENT_ID", content.documentId)
                    runBlocking {
                        val user = userPreference.getSession().first()
                        putExtra("USER_ID_KEY", user.userId)
                        putExtra("TOKEN", user.token)
                    }
                }

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context as Activity,
                    Pair(binding.imgPractice, "photo"),
                    Pair(binding.tvPractice, "name")
                )

                context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val practice = getItem(position)
        holder.bind(practice, userPreference)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ContentData>() {
            override fun areItemsTheSame(oldItem: ContentData, newItem: ContentData): Boolean {
                return oldItem.documentId == newItem.documentId
            }

            override fun areContentsTheSame(oldItem: ContentData, newItem: ContentData): Boolean {
                return oldItem == newItem
            }
        }
    }
}