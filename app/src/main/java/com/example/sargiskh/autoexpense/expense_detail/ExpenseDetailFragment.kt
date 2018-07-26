package com.example.sargiskh.autoexpense.expense_detail

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sargiskh.autoexpense.ExpenseModel
import com.example.sargiskh.autoexpense.R
import com.example.sargiskh.autoexpense.bottom_sheet.BottomSheetFragment
import com.example.sargiskh.autoexpense.database.ExpenseDBHelper
import java.text.SimpleDateFormat
import java.util.*


class ExpenseDetailFragment : Fragment(), CompoundButton.OnCheckedChangeListener, TextWatcher {

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (checkBoxSum.isChecked) {
            editTextCapacity.removeTextChangedListener(this)
            setCapacityFromSum()
            editTextCapacity.addTextChangedListener(this)
        } else if (checkBoxCapacity.isChecked) {
            editTextSum.removeTextChangedListener(this)
            setSumFromCapacity()
            editTextSum.addTextChangedListener(this)
        }
    }


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView!!.id) {
            R.id.checkBoxSum -> {
                if (isChecked) {
                    editTextSum.isEnabled = true
                    editTextCapacity.isEnabled = false

                    checkBoxCapacity.setOnCheckedChangeListener(null)
                    editTextCapacity.removeTextChangedListener(this)
                    setCapacityFromSum()
                    checkBoxCapacity.setOnCheckedChangeListener(this)
                    editTextCapacity.addTextChangedListener(this)

                    if (checkBoxCapacity.isChecked) {
                        checkBoxCapacity.isChecked = false
                    }

                } else {
                    editTextCapacity.isEnabled = true
                }

            }
            R.id.checkBoxCapacity-> {
                if (isChecked) {
                    editTextSum.isEnabled = false
                    editTextCapacity.isEnabled = true

                    checkBoxSum.setOnCheckedChangeListener(null)
                    editTextSum.removeTextChangedListener(this)
                    setSumFromCapacity()
                    checkBoxSum.setOnCheckedChangeListener(this)
                    editTextSum.addTextChangedListener(this)

                    if (checkBoxSum.isChecked) {
                        checkBoxSum.isChecked = false
                    }
                } else {
                    editTextSum.isEnabled = true
                }
            }
        }
    }


    private fun setCapacityFromSum() {

        if(editTextPrice.text.isEmpty() || editTextSum.text.isEmpty()) {
            editTextCapacity.setText("")
        } else if (editTextPrice.text.toString().equals("0") || editTextSum.text.toString().equals("0")) {
            editTextCapacity.setText("0")
        } else {
            var sum = editTextSum.text.toString().toFloat()
            var price = editTextPrice.text.toString().toFloat()

            val capacity = sum/price
            var capacityText = capacity.toString()

            if (capacity - capacity.toInt() == 0F) {

                capacityText = capacity.toInt().toString()
            }
            editTextCapacity.setText(capacityText)

        }
    }

    private fun setSumFromCapacity() {
        if(editTextPrice.text.isEmpty() || editTextCapacity.text.isEmpty()) {
            editTextSum.setText("")
        } else if (editTextPrice.text.toString().equals("0") || editTextCapacity.text.toString().equals("0")) {
            editTextSum.setText("0")
        } else {
            var capacity = editTextCapacity.text.toString().toFloat()
            var price = editTextPrice.text.toString().toFloat()
            val sum = capacity * price
            var sumText = sum.toString()

            if (sum  - sum.toInt() == 0F) {
                sumText = sum.toInt().toString()
            }
            editTextSum.setText(sumText)
        }
    }

    interface ExpenseDataBaseChangeListener {
        fun updatedExpense(expenseModel: ExpenseModel)
        fun createdNewExpense(expenseModel: ExpenseModel)
    }

    private lateinit var listener : ExpenseDataBaseChangeListener

    private lateinit var buttonSave: Button
    private lateinit var buttonDate: Button

    private lateinit var spinnerSum: Spinner
    private lateinit var spinnerCapacity: Spinner
    private lateinit var spinnerDistance: Spinner
    private lateinit var spinnerPricePerL: Spinner

    private lateinit var tabLayout: TabLayout

    private lateinit var editTextSum: EditText
    private lateinit var editTextCapacity: EditText
    private lateinit var editTextDistance: EditText
    private lateinit var editTextPrice: EditText

    private lateinit var checkBoxSum: CheckBox
    private lateinit var checkBoxCapacity: CheckBox

    private lateinit var expenseDBHelper : ExpenseDBHelper
    private val calendar = Calendar.getInstance()

    private lateinit var list_of_sum_items : Array<String>
    private lateinit var list_of_capacity_items : Array<String>
    private lateinit var list_of_distance_items : Array<String>
    private lateinit var list_of_price_per_l_items : Array<String>

    private var sharedPreferences: SharedPreferences? = null

    private val dateFormat = "dd/MM/yyyy"
    private val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)

    private var expenseModel : ExpenseModel?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("LOG_TAG", "DetailFragment : onCreate")
        retainInstance = true;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_expense_detail, container, false)

        findViews(view)

        sharedPreferences = activity!!.getSharedPreferences(BottomSheetFragment.PREFS_FILENAME, 0)
        expenseDBHelper = ExpenseDBHelper(context!!)
        expenseModel = if(arguments!!.getSerializable(INTENT_EXPENSE_DETAIL) != null) (arguments!!.getSerializable(INTENT_EXPENSE_DETAIL) as ExpenseModel) else null

        initListOfItems()
        setupAdapters()
        setupTabs()

        if (expenseModel == null) {
            setDefaultFields()
        } else {
            fillFields()
        }

        setListeners()

        return view
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ExpenseDataBaseChangeListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener == null
    }


    private fun findViews(view: View) {
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonDate = view.findViewById(R.id.buttonDate)

        spinnerSum = view.findViewById(R.id.spinnerSum)
        spinnerCapacity = view.findViewById(R.id.spinnerCapacity)
        spinnerDistance = view.findViewById(R.id.spinnerDistance)
        spinnerPricePerL = view.findViewById(R.id.spinnerPricePerL)

        tabLayout = view.findViewById(R.id.tabLayout)

        editTextSum = view.findViewById(R.id.editTextSum)
        editTextCapacity = view.findViewById(R.id.editTextCapacity)
        editTextDistance = view.findViewById(R.id.editTextDistance)
        editTextPrice = view.findViewById(R.id.editTextPricePerLiter)

        checkBoxSum = view.findViewById(R.id.checkBoxSum)
        checkBoxCapacity = view.findViewById(R.id.checkBoxCapacity)
    }

    private fun setListeners() {
        buttonSave.setOnClickListener(View.OnClickListener { saveExpense() })
        buttonDate.setOnClickListener(View.OnClickListener { openDatePickerDialog() })
        checkBoxSum.setOnCheckedChangeListener(this)
        checkBoxCapacity.setOnCheckedChangeListener(this)

        editTextSum.addTextChangedListener(this)
        editTextCapacity.addTextChangedListener(this)
        editTextPrice.addTextChangedListener(this)
    }


    private fun initListOfItems() {
        list_of_sum_items = resources.getStringArray(R.array.list_of_sum_items)
        list_of_capacity_items = resources.getStringArray(R.array.list_of_capacity_items)
        list_of_distance_items = resources.getStringArray(R.array.list_of_distance_items)
        list_of_price_per_l_items = resources.getStringArray(R.array.list_of_price_per_l_items)
    }

    private fun setupTabs() {
        // Iterate over all tabs and set the custom view
        for (i in 0..(tabLayout.getTabCount()-1) ) {
            val tab = tabLayout.getTabAt(i)
            tab!!.setCustomView(ToolbarPagerAdapter(context!!).getTabView(i))
        }
    }

    private fun setupAdapters() {
        val sumAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, list_of_sum_items)
        val capacityAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, list_of_capacity_items)
        val distanceAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, list_of_distance_items)
        val pricePerLAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, list_of_price_per_l_items)

        sumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        capacityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pricePerLAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerSum.adapter = sumAdapter
        spinnerCapacity.adapter = capacityAdapter
        spinnerDistance.adapter = distanceAdapter
        spinnerPricePerL.adapter = pricePerLAdapter
    }

    private fun fillFields() {

        checkBoxSum.setOnCheckedChangeListener(null)
        checkBoxCapacity.setOnCheckedChangeListener(null)
        if (checkBoxSum.isChecked) {
            editTextSum.isEnabled = true
            editTextCapacity.isEnabled = false
        } else if (checkBoxCapacity.isChecked) {
            editTextSum.isEnabled = false
            editTextCapacity.isEnabled = true
        }
        checkBoxSum.setOnCheckedChangeListener(this)
        checkBoxCapacity.setOnCheckedChangeListener(this)

        editTextSum.setText(expenseModel!!.sum.toString())
        editTextCapacity.setText(expenseModel!!.capacity.toString())
        editTextDistance.setText(expenseModel!!.distance.toString())
        editTextPrice.setText(expenseModel!!.price.toString())
        buttonDate.setText(expenseModel!!.date)

        spinnerSum.setSelection(list_of_sum_items!!.indexOf(expenseModel!!.sumType))
        spinnerCapacity.setSelection(list_of_capacity_items!!.indexOf(expenseModel!!.capacityType))
        spinnerDistance.setSelection(list_of_distance_items!!.indexOf(expenseModel!!.distanceType))
        spinnerPricePerL.setSelection(list_of_price_per_l_items!!.indexOf(expenseModel!!.priceType))
    }

    private fun updateExpenseModelFromFields(){
        expenseModel!!.sum = if (editTextSum.text.isEmpty()) 0 else editTextSum.text.toString().toInt()
        expenseModel!!.sumType = spinnerSum.selectedItem.toString()

        expenseModel!!.capacity = if (editTextCapacity.text.isEmpty()) 0.0F else editTextCapacity.text.toString().toFloat()
        expenseModel!!.capacityType = spinnerCapacity.selectedItem.toString()

        expenseModel!!.distance = if (editTextDistance.text.isEmpty()) 0 else editTextDistance.text.toString().toInt()
        expenseModel!!.distanceType = spinnerDistance.selectedItem.toString()

        expenseModel!!.price = if (editTextPrice.text.isEmpty()) 0 else editTextPrice.text.toString().toInt()
        expenseModel!!.priceType = spinnerPricePerL.selectedItem.toString()

        expenseModel!!.date = this.buttonDate.text.toString()
    }


    private fun saveExpense() {
        if (expenseModel == null) {
            createNewExpenseModelInDataBase()
        } else {
            updateExpenseModelFromFields()
            var isUpdated = expenseDBHelper.updateExpenseModel(expenseModel!!)
            if (isUpdated) {
                listener.updatedExpense(expenseModel!!)
            }
        }
        activity!!.supportFragmentManager.popBackStack();
    }

    private fun openDatePickerDialog() {
        if (expenseModel == null) {
            DatePickerDialog(activity, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        } else {
            val date = simpleDateFormat.parse(buttonDate.text.toString())
            calendar.time = date
            DatePickerDialog(activity, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun createNewExpenseModelInDataBase() {
        expenseModel = ExpenseModel()
        updateExpenseModelFromFields()
        var newExpenseId = expenseDBHelper.insertExpenseModel(expenseModel!!)
        expenseModel!!.id = newExpenseId.toInt()

        if (newExpenseId > -1) {
            listener.createdNewExpense(expenseModel!!)
        }
    }

    private val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        buttonDate.text = simpleDateFormat.format(calendar.time)
    }

    private fun setDefaultFields() {

        checkBoxSum.setOnCheckedChangeListener(null)
        checkBoxCapacity.setOnCheckedChangeListener(null)
        if (checkBoxSum.isChecked) {
            editTextSum.isEnabled = true
            editTextCapacity.isEnabled = false
        } else if (checkBoxCapacity.isChecked) {
            editTextSum.isEnabled = false
            editTextCapacity.isEnabled = true
        }
        checkBoxSum.setOnCheckedChangeListener(this)
        checkBoxCapacity.setOnCheckedChangeListener(this)

        buttonDate.text = simpleDateFormat.format(calendar.time)

        val defaultSumType = sharedPreferences!!.getString(BottomSheetFragment.DEFAULT_SUM_TYPE, "֏")
        val defaultCapacityType = sharedPreferences!!.getString(BottomSheetFragment.DEFAULT_CAPACITY_TYPE, "L")
        val defaultDistanceType = sharedPreferences!!.getString(BottomSheetFragment.DEFAULT_DISTANCE_TYPE, "Km")
        val defaultPricePerLiterType = sharedPreferences!!.getString(BottomSheetFragment.DEFAULT_PRICE_PER_LITER_TYPE, "֏")
        val defaultPricePerLiter = sharedPreferences!!.getString(BottomSheetFragment.DEFAULT_PRICE_PER_LITER, "0")

        spinnerSum.setSelection(list_of_sum_items!!.indexOf(defaultSumType))
        spinnerCapacity.setSelection(list_of_capacity_items!!.indexOf(defaultCapacityType))
        spinnerDistance.setSelection(list_of_distance_items!!.indexOf(defaultDistanceType))
        spinnerPricePerL.setSelection(list_of_price_per_l_items!!.indexOf(defaultPricePerLiterType))
        editTextPrice.setText(defaultPricePerLiter)
    }

    companion object {

        val INTENT_EXPENSE_DETAIL = "expense_detail"

        fun newInstance(args: Bundle): ExpenseDetailFragment {
            val fragment = ExpenseDetailFragment()
            fragment.arguments = args
            return fragment
        }

        fun setBundle(expenseModel: ExpenseModel?, position: Int): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(INTENT_EXPENSE_DETAIL, expenseModel)
            return bundle
        }
    }
}
