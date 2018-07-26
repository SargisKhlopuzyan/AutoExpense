package com.example.sargiskh.autoexpense

import java.io.Serializable

data class ExpenseModel(var isChecked : Boolean, var id : Int, var sum : Int, var sumType : String, var capacity : Float, var capacityType : String, var distance : Int, var distanceType : String, var price : Int, var priceType : String, var date : String) : Serializable {
    constructor(id : Int, sum : Int, sumType : String, capacity : Float, capacityType : String, distance : Int, distanceType : String, price : Int, priceType : String, date : String) : this(false, id, sum, sumType, capacity, capacityType, distance, distanceType, price, priceType, date)
    constructor(isChecked : Boolean, id : Int, sum : Int, capacity : Float, distance : Int, price : Int, date : String) : this(isChecked, id, sum, "֏", capacity, "L", distance, "Km", price, "֏", date)
    constructor(id : Int, sum : Int, capacity : Float, distance : Int, price : Int, date : String) : this(false, id, sum, "֏", capacity, "L", distance, "Km", price, "֏", date)
    constructor() : this(false,-1, 0, "", 0.0F, "", 0, "", 0, "", "")
}