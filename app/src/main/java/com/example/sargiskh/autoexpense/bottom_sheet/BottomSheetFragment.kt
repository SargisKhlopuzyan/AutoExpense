package com.example.sargiskh.autoexpense.bottom_sheet

import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.example.sargiskh.autoexpense.R

class BottomSheetFragment: BottomSheetDialogFragment() {

    private lateinit var list_of_sum_items : Array<String>
    private lateinit var list_of_capacity_items : Array<String>
    private lateinit var list_of_distance_items : Array<String>
    private lateinit var list_of_price_per_l_items : Array<String>

    private var sharedPreferences: SharedPreferences? = null

    private lateinit var spinnerSum: Spinner
    private lateinit var spinnerCapacity: Spinner
    private lateinit var spinnerDistance: Spinner
    private lateinit var spinnerPricePerL: Spinner

    private lateinit var editTextPricePerLiter: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);

        sharedPreferences = activity!!.getSharedPreferences(PREFS_FILENAME, 0)

        findViews(view)

        initListOfItems()
        setupAdapters()

        setViewsInitialsValues()
        setViewsChangedListener()
        return view
    }

    private fun findViews(view: View) {
        spinnerSum = view.findViewById(R.id.spinnerSum)
        spinnerCapacity = view.findViewById(R.id.spinnerCapacity)
        spinnerDistance = view.findViewById(R.id.spinnerDistance)
        spinnerPricePerL = view.findViewById(R.id.spinnerPricePerL)
        editTextPricePerLiter = view.findViewById(R.id.editTextPricePerLiter)
    }

    private fun initListOfItems() {
        list_of_sum_items = resources.getStringArray(R.array.list_of_sum_items)
        list_of_capacity_items = resources.getStringArray(R.array.list_of_capacity_items)
        list_of_distance_items = resources.getStringArray(R.array.list_of_distance_items)
        list_of_price_per_l_items = resources.getStringArray(R.array.list_of_price_per_l_items)
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

        spinnerSum.setAdapter(sumAdapter)
        spinnerCapacity.setAdapter(capacityAdapter)
        spinnerDistance.setAdapter(distanceAdapter)
        spinnerPricePerL.setAdapter(pricePerLAdapter)
    }

    private fun setViewsInitialsValues() {
        val defaultSumType = sharedPreferences!!.getString(DEFAULT_SUM_TYPE, "֏")
        val defaultCapacityType = sharedPreferences!!.getString(DEFAULT_CAPACITY_TYPE, "L")
        val defaultDistanceType = sharedPreferences!!.getString(DEFAULT_DISTANCE_TYPE, "Km")
        val defaultPricePerLiterType = sharedPreferences!!.getString(DEFAULT_PRICE_PER_LITER_TYPE, "֏")
        val defaultPricePerLiter = sharedPreferences!!.getString(DEFAULT_PRICE_PER_LITER, "0")

        spinnerSum.setSelection(list_of_sum_items!!.indexOf(defaultSumType))
        spinnerCapacity.setSelection(list_of_capacity_items!!.indexOf(defaultCapacityType))
        spinnerDistance.setSelection(list_of_distance_items!!.indexOf(defaultDistanceType))
        spinnerPricePerL.setSelection(list_of_price_per_l_items!!.indexOf(defaultPricePerLiterType))
        editTextPricePerLiter.setText(defaultPricePerLiter)
    }

    private fun setViewsChangedListener() {
        spinnerSum.onItemSelectedListener = spinnerOnItemSelectedListener
        spinnerCapacity.onItemSelectedListener = spinnerOnItemSelectedListener
        spinnerDistance.onItemSelectedListener = spinnerOnItemSelectedListener
        spinnerPricePerL.onItemSelectedListener = spinnerOnItemSelectedListener
        editTextPricePerLiter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val editor = sharedPreferences!!.edit()
                editor.putString(DEFAULT_PRICE_PER_LITER, s.toString())
                editor.apply()
            }
        })
    }

    private val spinnerOnItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val editor = sharedPreferences!!.edit()
            when(parent!!.id) {
                R.id.spinnerSum -> { editor.putString(DEFAULT_SUM_TYPE, list_of_sum_items.get(position))}
                R.id.spinnerCapacity -> { editor.putString(DEFAULT_CAPACITY_TYPE, list_of_capacity_items.get(position))}
                R.id.spinnerDistance -> { editor.putString(DEFAULT_DISTANCE_TYPE, list_of_distance_items.get(position))}
                R.id.spinnerPricePerL -> { editor.putString(DEFAULT_PRICE_PER_LITER_TYPE, list_of_price_per_l_items.get(position))}
            }
            editor.apply()
        }
    }

    companion object {
        val PREFS_FILENAME = "com.example.sargiskh.autoexpense.bottom_sheet.prefs"

        val DEFAULT_SUM_TYPE = "default_sum_type"
        val DEFAULT_CAPACITY_TYPE = "default_capacity_type"
        val DEFAULT_DISTANCE_TYPE = "default_distance_type"
        val DEFAULT_PRICE_PER_LITER_TYPE = "default_price_per_liter_type"
        val DEFAULT_PRICE_PER_LITER = "default_price_per_liter"
    }

}