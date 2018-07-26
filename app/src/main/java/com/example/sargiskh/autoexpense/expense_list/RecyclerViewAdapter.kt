package com.example.sargiskh.autoexpense.expense_list

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sargiskh.autoexpense.ExpenseModel
import com.example.sargiskh.autoexpense.R
import kotlinx.android.synthetic.main.recycler_view_item_layout.view.*

class RecyclerViewAdapter (val context : Context, val items : ArrayList<ExpenseModel>, val clickListener: (ExpenseModel, Int) -> Unit, val longClickListener: (ExpenseModel, Int) -> Boolean, val checkboxClickListener: (ExpenseModel, Int) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    private var isEditMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.get(position), position, isEditMode, clickListener, longClickListener, checkboxClickListener)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    fun  setEditMode(isDeleteMode: Boolean) {
        this.isEditMode = isDeleteMode

        if (!isDeleteMode) {
            for (item in items) {
                item.isChecked = false
            }
        }
    }

    fun  getEditMode()  = isEditMode
}

class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    val checkBox = view.checkBox
    val textViewSum = view.textViewSum
    val textViewCapacity = view.textViewCapacity
    val textViewDistance = view.textViewDistance
    val textViewPrice = view.textViewPrice
    val textViewDate = view.textViewDate

    fun bind(item: ExpenseModel, position: Int, isEditMode: Boolean, clickListener: (ExpenseModel, Int) -> Unit, longClickListener: (ExpenseModel, Int) -> Boolean, checkboxClickListener: (ExpenseModel, Int) -> Unit): Unit = with(itemView) {

        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = item.isChecked
        checkBox.visibility = if(isEditMode) View.VISIBLE else View.GONE

        textViewSum.setText(item.sum.toString() + " " + item.sumType)
        textViewCapacity.setText(item.capacity.toString() + " " + item.capacityType)
        textViewDistance.setText(item.distance.toString() + " " + item.distanceType)
        textViewPrice.setText(item.price.toString() + " " + item.priceType)
        textViewDate.setText(item.date)

        setOnClickListener {
            if (isEditMode) {
                checkBox.isChecked = !item.isChecked
            } else {
                clickListener.invoke(item, position)
            }
        }

        setOnLongClickListener{
            if (isEditMode) {
                checkBox.isChecked = !item.isChecked
                true

            } else {
                checkBox.isChecked = true
                true
//                longClickListener.invoke(item, position)
            }
        }

        checkBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            item.isChecked = isChecked
            checkboxClickListener.invoke(item, position)
        }
    }

}
