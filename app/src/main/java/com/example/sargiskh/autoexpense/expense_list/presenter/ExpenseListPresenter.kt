package com.example.sargiskh.autoexpense.expense_list.presenter

import android.content.Context
import com.example.sargiskh.autoexpense.ExpenseModel
import com.example.sargiskh.autoexpense.database.ExpenseDBHelper
import com.example.sargiskh.autoexpense.expense_list.ui.ExpenseListFragmentInterface

class ExpenseListPresenter(var expenseListFragmentInterface: ExpenseListFragmentInterface, var context: Context): ExpenseListPresenterInterface {

    private var expenseModelList: ArrayList<ExpenseModel> = ArrayList()

    fun getExpenseModelList() = expenseModelList


    override fun retrieveAllExpenseList() {
        val expenseDBHelper = ExpenseDBHelper(context)
        expenseModelList.clear()
        expenseModelList.addAll(expenseDBHelper.readAllExpenses())
        expenseListFragmentInterface.updateRecyclerView()
    }

    override fun addExpense(expenseModel: ExpenseModel) {
        expenseModelList.add(expenseModel)
        expenseListFragmentInterface.updateRecyclerView()
    }

    override fun deleteSelectedExpenses() {
        val count = expenseModelList.count()- 1
        for (i in count downTo 0) {
            if (expenseModelList[i].isChecked) {
                val expenseDBHelper = ExpenseDBHelper(context)
                val isDeleted = expenseDBHelper.deleteExpense(expenseModelList[i].id)
                if (isDeleted) {
                    expenseModelList.remove(expenseModelList[i])
                }
            }
        }
        expenseListFragmentInterface.updateRecyclerView()
    }

    override fun deleteAllExpenses() {
        val count = expenseModelList.count()- 1
        for (i in count downTo 0) {
            val expenseDBHelper = ExpenseDBHelper(context)
            val isDeleted = expenseDBHelper.deleteExpense(expenseModelList[i].id)
            if (isDeleted) {
                expenseModelList.remove(expenseModelList[i])
            }
        }
        expenseListFragmentInterface.updateRecyclerView()
    }
}