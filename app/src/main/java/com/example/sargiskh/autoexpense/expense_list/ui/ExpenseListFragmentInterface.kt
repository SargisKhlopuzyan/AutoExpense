package com.example.sargiskh.autoexpense.expense_list.ui

import com.example.sargiskh.autoexpense.ExpenseModel

interface ExpenseListFragmentInterface {
    fun updateRecyclerView()
    fun displayError(s: String)
    fun showToast(str: String)
}