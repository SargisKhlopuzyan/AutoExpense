package com.example.sargiskh.autoexpense.database.db_rx

import android.content.Context
import com.example.sargiskh.autoexpense.ExpenseModel
import com.example.sargiskh.autoexpense.database.ExpenseDBHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class DbRxHelper {

    fun getObservableForAllExpenses(context: Context) : Observable<ArrayList<ExpenseModel>> {

        return Observable.fromCallable { getAllExpenseListFromDb(context) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        // With Delay
        /*
        return Observable.timer(4, TimeUnit.SECONDS)
                .map { t -> getAllExpenseListFromDb(context) }
                .observeOn(AndroidSchedulers.mainThread())
        */

    }

    private fun getAllExpenseListFromDb(context: Context): ArrayList<ExpenseModel> {
        val expenseDBHelper = ExpenseDBHelper(context)
        return expenseDBHelper.readAllExpenses()
    }

}