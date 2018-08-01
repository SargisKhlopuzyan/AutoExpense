package com.example.sargiskh.autoexpense.expense_list.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.example.sargiskh.autoexpense.ExpenseModel
import com.example.sargiskh.autoexpense.R
import com.example.sargiskh.autoexpense.bottom_sheet.BottomSheetFragment
import com.example.sargiskh.autoexpense.expense_detail.ExpenseDetailFragment
import com.example.sargiskh.autoexpense.expense_list.adapter.RecyclerViewAdapter
import com.example.sargiskh.autoexpense.expense_list.presenter.ExpenseListPresenter

class ExpenseListFragment : Fragment() , ExpenseListFragmentInterface , CompoundButton.OnCheckedChangeListener {

    interface EditOrCreateNewExpenseInterface {
        fun editExistingExpense(bundle: Bundle)
        fun createNewExpense(bundle: Bundle)
    }

    private lateinit var editOrCreateNewExpenseInterface : EditOrCreateNewExpenseInterface
    private lateinit var expenseListPresenter:  ExpenseListPresenter

    override fun updateRecyclerView() {
        (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
    }

    override fun displayError(s: String) {
        showToast(s)
    }

    override fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show()
    }


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        expenseListPresenter = ExpenseListPresenter(this, activity!!.applicationContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_expense_list, container, false)
        findViews(view)
        setupViews()
        setupListeners()
        expenseListPresenter.retrieveAllExpenseList()
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
        if (isChecked) {
            for (expenses in expenseListPresenter.getExpenseModelList()) {
                expenses.isChecked = true
            }
        }
        else {
            for (expenses in expenseListPresenter.getExpenseModelList()) {
                expenses.isChecked = false
            }
        }
        setEditModeActionView()
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

    private fun setupViews() {

        val adapter = RecyclerViewAdapter(context!!, expenseListPresenter.getExpenseModelList(),
                { expenseModel: ExpenseModel, position: Int -> recyclerViewItemClicked(expenseModel, position) },
                { expenseModel: ExpenseModel, position: Int -> recyclerViewItemLongClicked(expenseModel, position) },
                { expenseModel: ExpenseModel, position: Int -> recyclerViewItemCheckboxClicked(expenseModel, position) })

        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.adapter = adapter;

        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun setupListeners() {

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
                expenseListPresenter.deleteSelectedExpenses()
            }
        })

        checkBoxSelectAll.setOnCheckedChangeListener(this)

        buttonSettings.setOnClickListener(View.OnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(activity!!.supportFragmentManager, bottomSheetFragment.tag)
        })
    }


    private fun recyclerViewItemClicked(expenseModel: ExpenseModel, position: Int) {
        var bundle = ExpenseDetailFragment.setBundle(expenseModel, position)
        editOrCreateNewExpenseInterface!!.editExistingExpense(bundle)
    }

    private fun recyclerViewItemLongClicked(expenseModel: ExpenseModel, position: Int): Boolean {
        setEditMode(true, true)
        return true
    }

    private fun recyclerViewItemCheckboxClicked(expenseModel: ExpenseModel, position: Int): Boolean {
        setEditMode(true, true)
        return true
    }


    private fun setEditModeActionView() {

        var count = 0
        for (expense in expenseListPresenter.getExpenseModelList()) {
            if (expense.isChecked) {
                count++
            }
        }

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
            if (count == expenseListPresenter.getExpenseModelList().size) {
                checkBoxSelectAll.isChecked = true
            } else {
                checkBoxSelectAll.isChecked = false
            }
            checkBoxSelectAll.setOnCheckedChangeListener(this)
        }
    }

    public fun setEditMode(isEditMode: Boolean, isUpdateRecyclerViewAdapter: Boolean) {

        if (isEditMode) {
            linearLayoutEditMode.visibility = View.VISIBLE
            linearLayoutNormalMode.visibility = View.GONE

            checkBoxHeader.visibility = View.INVISIBLE

            setEditModeActionView()
        } else {
            linearLayoutEditMode.visibility = View.GONE
            linearLayoutNormalMode.visibility = View.VISIBLE

            checkBoxSelectAll.setOnCheckedChangeListener(null)
            checkBoxSelectAll.isChecked = false
            checkBoxSelectAll.setOnCheckedChangeListener(this)

            checkBoxHeader.visibility = View.GONE
        }

        (recyclerView.adapter as RecyclerViewAdapter).setEditMode(isEditMode)
        if (isUpdateRecyclerViewAdapter) {
            (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
        }
    }

    public fun getEditMode() = (recyclerView.adapter as RecyclerViewAdapter).getEditMode()

    public fun addExpenseToRecyclerView(expenseModel: ExpenseModel) {
        expenseListPresenter.addExpense(expenseModel)
    }

}
