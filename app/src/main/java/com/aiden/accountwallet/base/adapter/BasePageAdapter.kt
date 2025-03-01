package com.aiden.accountwallet.base.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.util.Logger

abstract class BasePageAdapter<M: Any, V: ViewDataBinding>(
    private val context: Context,
    callBack: DiffUtil.ItemCallback<M>
) : PagingDataAdapter<M, RecyclerView.ViewHolder>(callBack) {

    protected abstract fun getDataBindingConfig(): DataBindingConfig

    protected abstract fun onBindItem(binding: V, position:Int, item: M?, holder: RecyclerView.ViewHolder)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding:V? = DataBindingUtil.getBinding(holder.itemView)
        val item:M? = getItem(position)
        binding?.let { onBindItem(it, position, item, holder) }
        binding?.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Logger.d("onCreateViewHolder")
        // config load
        val dbConfig: DataBindingConfig = getDataBindingConfig()
        val binding: V = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            dbConfig.layout,
            parent,
            false
        )
        // if has ViewModel..add ViewModel..!
        binding.setVariable(dbConfig.vmVariableId, dbConfig.stateViewModel)
        // add other data binding params...
        val params: SparseArray<Any?> = dbConfig.bindingParams
        params.forEach { key, value ->
            binding.setVariable(key, value)
        }

        return BasePageViewHolder(binding.root)
    }

    class BasePageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}