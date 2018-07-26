package com.example.sargiskh.autoexpense.expense_list

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.example.sargiskh.autoexpense.ExpenseModel
import com.example.sargiskh.autoexpense.R
import com.example.sargiskh.autoexpense.bottom_sheet.BottomSheetFragment
import com.example.sargiskh.autoexpense.database.ExpenseDBHelper
import com.example.sargiskh.autoexpense.expense_detail.ExpenseDetailFragment

class ExpenseListFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    interface EditOrCreateNewExpenseInterface {
        fun editExistingExpense(bundle: Bundle)
        fun createNewExpense(bundle: Bundle)
    }

    private lateinit var editOrCreateNewExpenseInterface : EditOrCreateNewExpenseInterface

    private lateinit var buttonSettings: Button

    private lateinit var checkBoxSelectAll: CheckBox
    private lateinit var textViewCheckboxCount: TextView

    private lateinit var linearLayoutEditMode: LinearLayout
    private lateinit var linearLayoutEditModeActions: LinearLayout
    private lateinit var linearLayoutNormalMode: LinearLayout
    private lateinit var buttonShare: Button
    private lateinit var buttonEdit: Button
    private lateinit var buttonNew: Button
    private lateinit var buttonDelete: Button

    private lateinit var checkBoxHeader: CheckBox
    private lateinit var recyclerView: RecyclerView

    private lateinit var expenseDBHelper : ExpenseDBHelper

    private lateinit var allExpenses : ArrayList<ExpenseModel>;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("LOG_TAG", "ListFragment : onCreate")
        retainInstance = true;
        expenseDBHelper = ExpenseDBHelper(context!!)
        allExpenses = expenseDBHelper.readAllExpenses()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_expense_list, container, false)

        findViews(view)

        val adapter = RecyclerViewAdapter(context!!, allExpenses,
                { expenseModel: ExpenseModel, position: Int -> recyclerViewItemClicked(expenseModel, position) },
                { expenseModel: ExpenseModel, position: Int -> recyclerViewItemLongClicked(expenseModel, position) },
                { expenseModel: ExpenseModel, position: Int -> recyclerViewItemCheckboxClicked(expenseModel, position) })

        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.adapter = adapter;

        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        initListeners()

        return view;
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is EditOrCreateNewExpenseInterface) {
            editOrCreateNewExpenseInterface = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        editOrCreateNewExpenseInterface == null
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        Log.e("LOG_TAG", "onCheckedChanged: isChecked: " + isChecked)
        if (isChecked) {
            for (expenses in allExpenses) {
                expenses.isChecked = true
            }
        }
        else {
            for (expenses in allExpenses) {
                expenses.isChecked = false
            }
        }
        setEditModeAction()
        updateRecyclerView()
    }


    private fun findViews(view: View) {
        linearLayoutEditMode = view.findViewById(R.id.linearLayoutEditMode)
        linearLayoutEditModeActions = view.findViewById(R.id.linearLayoutEditModeActions)
        checkBoxSelectAll = view.findViewById(R.id.checkBoxSelectAll)
        textViewCheckboxCount = view.findViewById(R.id.textViewCheckboxCount)
        buttonShare = view.findViewById(R.id.buttonShare)
        buttonDelete = view.findViewById(R.id.buttonDelete)

        linearLayoutNormalMode = view.findViewById(R.id.linearLayoutNormalMode)
        buttonSettings = view.findViewById(R.id.buttonSettings)
        buttonEdit = view.findViewById(R.id.buttonEdit)
        buttonNew = view.findViewById(R.id.buttonNew)

        checkBoxHeader = view.findViewById(R.id.checkBoxHeader)
        recyclerView = view.findViewById(R.id.recyclerView)
    }

    private fun initListeners() {

        buttonNew.setOnClickListener(View.OnClickListener {
            var bundle = ExpenseDetailFragment.setBundle(null, -1)
            editOrCreateNewExpenseInterface.createNewExpense(bundle)
        })

        buttonEdit.setOnClickListener(View.OnClickListener {
            if (!getEditMode()) {
                setEditMode(true, true)
            }
        })

        buttonDelete.setOnClickListener(View.OnClickListener {
            if (getEditMode()) {
                deleteSelectedExpenses()
            }
        })

        checkBoxSelectAll.setOnCheckedChangeListener(this)

        buttonSettings.setOnClickListener(View.OnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(activity!!.supportFragmentManager, bottomSheetFragment.tag)
        })
    }


    private fun recyclerViewItemClicked(expenseModel: ExpenseModel, position: Int) {
        Log.e("LOG_TAG", "recyclerViewItemClicked")
        var bundle = ExpenseDetailFragment.setBundle(expenseModel, position)
        editOrCreateNewExpenseInterface!!.editExistingExpense(bundle)
    }

    private fun recyclerViewItemLongClicked(expenseModel: ExpenseModel, position: Int): Boolean {
        Log.e("LOG_TAG", "recyclerViewItemLongClicked")
        setEditMode(true, true)
        return true
    }

    private fun recyclerViewItemCheckboxClicked(expenseModel: ExpenseModel, position: Int): Boolean {
        Log.e("LOG_TAG", "recyclerViewItemCheckboxClicked")
        setEditMode(true, true)
        return true
    }



    private fun deleteSelectedExpenses() {
        val count = allExpenses.count()- 1
        for (i in count downTo 0) {
            if (allExpenses[i].isChecked) {
                val deleted = expenseDBHelper.deleteExpense(allExpenses[i].id)
                allExpenses.remove(allExpenses[i])
            }
        }
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
    }

    fun addExspenseToRecyclerView(expense: ExpenseModel) {
        allExpenses.add(expense)
        (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
    }

    fun setEditMode(isEditMode: Boolean, isUpdate: Boolean) {

        if (isEditMode) {
            linearLayoutEditMode.visibility = View.VISIBLE
            linearLayoutNormalMode.visibility = View.GONE

            checkBoxHeader.visibility = View.INVISIBLE

            setEditModeAction()
        } else {
            linearLayoutEditMode.visibility = View.GONE
            linearLayoutNormalMode.visibility = View.VISIBLE

            checkBoxSelectAll.setOnCheckedChangeListener(null)
            checkBoxSelectAll.isChecked = false
            checkBoxSelectAll.setOnCheckedChangeListener(this)

            checkBoxHeader.visibility = View.GONE
        }

        (recyclerView.adapter as RecyclerViewAdapter).setEditMode(isEditMode)
        if (isUpdate) {
            (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
        }
    }

    fun getEditMode() = (recyclerView.adapter as RecyclerViewAdapter).getEditMode()

    private fun setEditModeAction() {

        var count = 0
        for (expense in allExpenses) {
            if (expense.isChecked) {
                count++
            }
        }

        Log.e("LOG_TAG", "count: " + count)

        if (count == 0) {
            linearLayoutEditModeActions.visibility = View.GONE
            textViewCheckboxCount.setText(resources.getString(R.string.select_expenses))

            checkBoxSelectAll.setOnCheckedChangeListener(null)
            checkBoxSelectAll.isChecked = false
            checkBoxSelectAll.setOnCheckedChangeListener(this)

        } else {
            linearLayoutEditModeActions.visibility = View.VISIBLE
            textViewCheckboxCount.setText(count.toString())

            checkBoxSelectAll.setOnCheckedChangeListener(null)
            if (count == allExpenses.size) {
                checkBoxSelectAll.isChecked = true
            } else {
                checkBoxSelectAll.isChecked = false
            }
            checkBoxSelectAll.setOnCheckedChangeListener(this)
        }
    }

}
