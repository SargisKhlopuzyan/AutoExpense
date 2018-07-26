package com.example.sargiskh.autoexpense.database

import android.provider.BaseColumns

class DBContract {

    class ExpenseModelEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "expenses"
            val COLUMN_ID = "id"
            val COLUMN_SUM = "sum"
            val COLUMN_SUM_TYPE = "sumType"
            val COLUMN_CAPACITY = "capacity"
            val COLUMN_CAPACITY_TYPE = "capacityType"
            val COLUMN_DISTANCE = "distance"
            val COLUMN_DISTANCE_TYPE = "distanceType"
            val COLUMN_PRICE = "price"
            val COLUMN_PRICE_TYPE = "priceType"
            val COLUMN_DATE = "date"
        }
    }

}