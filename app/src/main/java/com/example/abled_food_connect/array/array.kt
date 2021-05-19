package com.example.abled_food_connect.array

interface array {
   fun numArray(): ArrayList<Int>


}
class age : array {
    override fun numArray(): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in 18..100) {
            list.add(i)

        }
        return list
    }

}

class numOfPeople : array {
    override fun numArray(): ArrayList<Int> {
        val list = ArrayList<Int>()

        for (i in 1..3) {
            list.add(i)

        }
        return list
    }
}