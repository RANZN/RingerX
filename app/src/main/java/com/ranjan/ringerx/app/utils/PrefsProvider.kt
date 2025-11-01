package com.ranjan.ringerx.app.utils

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri


class PrefsProvider : ContentProvider() {

    override fun onCreate() = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val prefs = context!!.getSharedPreferences(Prefs.PREFS_FILE, Context.MODE_PRIVATE)
        val json = prefs.getString(Prefs.KEY_EVENTS_JSON, "[]") ?: "[]"

        val cursor = MatrixCursor(arrayOf("json"))
        cursor.addRow(arrayOf(json))
        return cursor
    }

    override fun getType(uri: Uri) = "text/plain"
    override fun insert(uri: Uri, values: ContentValues?) = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = 0
}
