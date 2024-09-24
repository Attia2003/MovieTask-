package com.example.taskmovie.apis

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

fun Fragment.showmessage(message :String
                         , posActionName:String?=null
                         , posAction: DialogInterface.OnClickListener?=null,
                         negActionName:String?=null,
                         negAction: DialogInterface.OnClickListener?=null
): AlertDialog {
    val builder  = AlertDialog.Builder(context!!)
    builder.setMessage(message)
    if (posActionName!=null){
        builder.setPositiveButton(posActionName,posAction)
    }
   if (negAction!=null){
       builder.setNegativeButton(negActionName,negAction)
   }
  return  builder.show()
}
fun Activity.showmessage(message :String
                         , posActionName:String?=null
                         , posAction: DialogInterface.OnClickListener?=null,
                         negActionName:String?=null,
                         negAction: DialogInterface.OnClickListener?=null
): AlertDialog {
    val builder  = AlertDialog.Builder(this)
    builder.setMessage(message)
    if (posActionName!=null){
        builder.setPositiveButton(posActionName,posAction)
    }
    if (negAction!=null){
        builder.setNegativeButton(negActionName,negAction)
    }
    return  builder.show()
}

