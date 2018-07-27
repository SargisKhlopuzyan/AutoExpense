package com.example.sargiskh.autoexpense.expense_list.presenter

import com.example.sargiskh.autoexpense.ExpenseModel

interface ExpenseListPresenterInterface {
    fun retrieveAllExpenseList()
    fun addExpense(expenseModel: ExpenseModel)
    fun deleteSelectedExpenses()
    fun deleteAllExpenses()
}