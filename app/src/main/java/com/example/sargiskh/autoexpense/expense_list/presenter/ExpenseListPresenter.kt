package com.example.sargiskh.autoexpense.expense_list.presenter

import android.content.Context
import com.example.sargiskh.autoexpense.ExpenseModel
import com.example.sargiskh.autoexpense.database.ExpenseDBHelper
import com.example.sargiskh.autoexpense.database.db_rx.DbRxHelper
import com.example.sargiskh.autoexpense.expense_list.ui.ExpenseListFragmentInterface
import io.reactivex.observers.DisposableObserver

class ExpenseListPresenter(var expenseListFragmentInterface: ExpenseListFragmentInterface, var context: Context): ExpenseListPresenterInterface {

    private var expenseModelList: ArrayList<ExpenseModel> = ArrayList()

    fun getExpenseModelList() = expenseModelList


    override fun retrieveAllExpenseList() {
        // Rx Version
        DbRxHelper().getObservableForAllExpenses(context).subscribe(disposableObserver)

        // Normal Db call
        /*
        val expenseDBHelper = ExpenseDBHelper(context)
        expenseModelList.clear()
        expenseModelList.addAll(expenseDBHelper.readAllExpenses())
        expenseListFragmentInterface.updateRecyclerView()
        */
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


    private val disposableObserver = object : DisposableObserver<ArrayList<ExpenseModel>>() {

        override fun onComplete() {

        }

        override fun onNext(t: ArrayList<ExpenseModel>) {
            expenseModelList.clear()
            expenseModelList.addAll(t)
            expenseListFragmentInterface.updateRecyclerView()
        }

        override fun onError(e: Throwable) {
            expenseListFragmentInterface.displayError("Error: " + e)
        }
    }

}