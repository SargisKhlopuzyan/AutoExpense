package com.example.sargiskh.autoexpense

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.sargiskh.autoexpense.expense_detail.ExpenseDetailFragment
import com.example.sargiskh.autoexpense.expense_list.ui.ExpenseListFragment


class MainActivity : AppCompatActivity(), ExpenseListFragment.EditOrCreateNewExpenseInterface, ExpenseDetailFragment.ExpenseDataBaseChangeListener {

    override fun updatedExpense(expenseModel: ExpenseModel) {

    }

    override fun createdNewExpense(expenseModel: ExpenseModel) {
        Log.e("LOG_TAG", "createdNewExpense")
        val fragment = supportFragmentManager.findFragmentByTag("ExpenseListFragment")
        if (fragment != null && fragment is ExpenseListFragment) {
            fragment.addExpenseToRecyclerView(expenseModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            Log.e("LOG_TAG", "savedInstanceState == null")
            val fragment = ExpenseListFragment()
            supportFragmentManager.beginTransaction().replace(R.id.replaceFrame, fragment, "ExpenseListFragment").commit()
        } else {
            Log.e("LOG_TAG", "savedInstanceState != null")
            //Restore the fragment's instance
//            expenseListFragment= supportFragmentManager.getFragment(savedInstanceState, "ExpenseListFragment") as ExpenseListFragment
        }
    }

    override fun editExistingExpense(bundle: Bundle) {
        val fragment = ExpenseDetailFragment.newInstance(bundle)
        supportFragmentManager.beginTransaction().replace(R.id.replaceFrame, fragment).addToBackStack(null).commit()
    }

    override fun createNewExpense(bundle: Bundle) {
        val fragment = ExpenseDetailFragment.newInstance(bundle)
        supportFragmentManager.beginTransaction().replace(R.id.replaceFrame, fragment).addToBackStack(null).commit()
    }

    override fun onBackPressed() {
        val activeFragment = supportFragmentManager.findFragmentById(R.id.replaceFrame)
        if (activeFragment != null && activeFragment is ExpenseListFragment) {
            if (activeFragment.getEditMode()) {
                activeFragment.setEditMode(false, true)
            } else {
                supportFragmentManager.popBackStack()
            }
        } else {
            super.onBackPressed();
        }
    }
}
