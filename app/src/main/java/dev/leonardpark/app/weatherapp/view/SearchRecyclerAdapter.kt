package dev.leonardpark.app.weatherapp.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.leonardpark.app.weatherapp.databinding.ItemRecentSearchBinding
import dev.leonardpark.app.weatherapp.db.SearchEntity
import java.util.*

class SearchRecyclerAdapter(
  private val listener: SearchRecyclerInterface
) : RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder>() {

  private val itemList: MutableList<SearchEntity> = ArrayList()

  fun setItems(items: List<SearchEntity>) {
    val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
      override fun getOldListSize(): Int {
        return itemList.size
      }

      override fun getNewListSize(): Int {
        return items.size
      }

      override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newItem = items[newItemPosition]
        val oldItem = itemList[oldItemPosition]
        return oldItem.getText() == newItem.getText()
      }

      override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = itemList[oldItemPosition]
        val newItem = items[newItemPosition]
        return oldItem.getText() == newItem.getText() && newItem.getTimestamp() == oldItem.getTimestamp()
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
    holder.bindView(itemList[position], listener)
  }

  override fun getItemCount(): Int = itemList.size

  class SearchViewHolder(
    private val binding: ItemRecentSearchBinding
  ) : RecyclerView.ViewHolder(binding.root) {
    fun bindView(entity: SearchEntity, listener: SearchRecyclerInterface) {
      binding.tvText.text = entity.getText()
      binding.itemHolder.setOnClickListener {
        listener.onSearchItemClicked(entity.getText())
      }
      binding.imgDelete.setOnClickListener {
        listener.onSearchDeleteClicked(entity)
      }
    }
  }
}