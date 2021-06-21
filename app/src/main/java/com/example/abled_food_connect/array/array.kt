package com.example.abled_food_connect.array

interface array {
   fun numArray(num:Int): ArrayList<Int>



}

class age  {
     fun numArray(): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in 18..100) {
            list.add(i)

        }
        return list
    }

}
class minimumAge : array {
    override fun numArray(num:Int): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in 18..num) {
            list.add(i)

        }
        return list
    }

}

class maximumAge : array {
    override fun numArray(num:Int): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in num..100) {
            list.add(i)

        }
        return list
    }

}

class numOfPeople : array {
    override fun numArray(num:Int): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in 1..num) {
            list.add(i)

        }
        return list
    }
}