package dev.leonardpark.app.weatherapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.leonardpark.app.weatherapp.R
import dev.leonardpark.app.weatherapp.databinding.ItemRecentSearchBinding
import dev.leonardpark.app.weatherapp.db.SearchEntity
import java.util.*

class SearchRecyclerAdapter(
  private val context: Context,
  private val isDark: Boolean,
  private val listener: SearchRecyclerInterface
) : RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder>() {

  private val itemList: MutableList<SearchEntity> = ArrayList()

  fun setItems(items: List<SearchEntity>) {
    val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
      override fun getOldListSize(): Int = itemList.size

      override fun getNewListSize(): Int = items.size

      override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newItem = items[newItemPosition]
        val oldItem = itemList[oldItemPosition]
        return oldItem.getText() == newItem.getText()
      }

      override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = itemList[oldItemPosition]
        val newItem = items[newItemPosition]
        return oldItem.getText() == newItem.getText() && oldItem.getTimestamp() == newItem.getTimestamp()
      }
    })
    itemList.clear()
    itemList.addAll(items)
    diffResult.dispatchUpdatesTo(this)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
    return SearchViewHolder(
      ItemRecentSearchBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
    holder.bindView(isDark, context, itemList[position], listener)
  }

  override fun getItemCount(): Int = itemList.size

  class SearchViewHolder(
    private val binding: ItemRecentSearchBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bindView(
      isDark: Boolean,
      context: Context,
      entity: SearchEntity,
      listener: SearchRecyclerInterface
    ) {

      binding.itemHolder.setOnClickListener {
        listener.onSearchItemClicked(entity.getText())
      }

      binding.imgSearch.setColorFilter(
        ContextCompat.getColor(
          context,
          if (isDark) R.color.white else R.color.black
        ), android.graphics.PorterDuff.Mode.SRC_IN
      )

      binding.tvText.apply {
        text = entity.getText()
        setTextColor(
          ContextCompat.getColor(
            context,
            if (isDark) R.color.white else R.color.black
          )
        )
      }

      binding.imgDelete.apply {
        setOnClickListener {
          listener.onSearchDeleteClicked(entity)
        }
        setColorFilter(
          ContextCompat.getColor(
            context,
            if (isDark) R.color.white else R.color.black
          ), android.graphics.PorterDuff.Mode.SRC_IN
        )
      }
    }
  }
}