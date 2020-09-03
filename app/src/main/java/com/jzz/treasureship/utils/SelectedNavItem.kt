package com.jzz.treasureship.utils

class SelectedNavItem {

    companion object {
        var selectedNavItem = 0
        val TODO = 0
        val DATEEVENT = 1


        fun getSlectedNavItem(): Int {
            return selectedNavItem
        }

        fun setSectedNavItem(slectedNavItem: Int) {
            SelectedNavItem.selectedNavItem = slectedNavItem
        }
    }
}