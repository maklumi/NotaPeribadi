package com.akemal.notaperibadi;


import android.support.v4.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev on 6/10/15.
 */
public class NotesLoader extends AsyncTaskLoader<List<Note>> {

    private List<Note> mNotes;
    private ContentResolver mContentResolver;
    private Cursor mCursor; //to navigate the record
    private int mType; // Reminder of a note

    public NotesLoader(Context context, ContentResolver contentResolver, int type) {
        super(context);
        mContentResolver = contentResolver;
        mType = type;
    }

    @Override
    public List<Note> loadInBackground() {
        List<Note> entries = new ArrayList<>();
        String[] projection = {
                BaseColumns._ID,
                NotesContract.NotesColumns.NOTES_TITLE,
                NotesContract.NotesColumns.NOTES_DESCRIPTION,
                NotesContract.NotesColumns.NOTES_TYPE,
                NotesContract.NotesColumns.NOTES_DATE,
                NotesContract.NotesColumns.NOTES_TIME,
                NotesContract.NotesColumns.NOTES_IMAGE,
                NotesContract.NotesColumns.NOTES_IMAGE_STORAGE_SELECTION };

        Uri uri = NotesContract.URI_TABLE;
        mCursor = mContentResolver.query(uri, projection, null, null, BaseColumns._ID + " DESC");
        if(mCursor != null) {
            if(mCursor.moveToFirst()) {
                do {
                    String date = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesColumns.NOTES_DATE));
                    String time = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesColumns.NOTES_TIME));
                    String type = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesColumns.NOTES_TYPE));
                    String title = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesColumns.NOTES_TITLE));
                    String description = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesColumns.NOTES_DESCRIPTION));
                    String imagePath = mCursor.getString(mCursor.getColumnIndex(NotesContract.NotesColumns.NOTES_IMAGE));
                    int imageSelection = mCursor.getInt(mCursor.getColumnIndex(NotesContract.NotesColumns.NOTES_IMAGE_STORAGE_SELECTION));
                    int _id = mCursor.getInt(mCursor.getColumnIndex(BaseColumns._ID)); //primary key is underscore _id by convention
                    if(mType == BaseActivity.NOTES) {
                        if(time.equals(AppConstant.NO_TIME)) { //NO TIME means no note recorded
                            time = "";
                            Note note = new Note(title, description, date, time, _id, imageSelection, type);
                            if(!imagePath.equals(AppConstant.NO_IMAGE)) { //image is optional, so if presence do this
                                if(imageSelection == AppConstant.DEVICE_SELECTION) {
                                    note.setBitmap(imagePath); //since coming from local drive
                                } else {
                                    // Is a Google Drive or Dropbox Image
                                }
                            } else {
                                note.setImagePath(AppConstant.NO_IMAGE);
                            }

                            entries.add(note);
                        }

                    } else if(mType == BaseActivity.REMINDERS) {
                        if(time.equals(AppConstant.NO_TIME)) {
                            Note note = new Note(title, description, date, time, _id, imageSelection, type);
                            if(!imagePath.equals(AppConstant.NO_IMAGE)) {
                                if(imageSelection == AppConstant.DEVICE_SELECTION) {
                                    note.setBitmap(imagePath);
                                } else {
                                    // Is a Google Drive or Dropbox Image
                                }
                            } else {
                                note.setImagePath(AppConstant.NO_IMAGE);
                            }

                            entries.add(note);
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid type = " + mType);
                    }
                } while(mCursor.moveToNext());
            }
        }

        return entries;
    }


    @Override
    public void deliverResult(List<Note> notes) {
        if (isReset()) {
            if(notes != null) {
                releaseResources();
                return;
            }
        }
        List<Note> oldNotes = mNotes;
        mNotes = notes;
        if(isStarted()) {
            super.deliverResult(notes);
        }
        if(oldNotes != null && oldNotes != notes) { // there are changes in note
            releaseResources();
        }
    }

    @Override
    protected void onStartLoading() {
        if(mNotes != null) {
            deliverResult(mNotes);
        }
        if(takeContentChanged()) {
            forceLoad();
        } else if(mNotes == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if(mNotes != null) {
            releaseResources();
            mNotes = null;
        }
    }


    @Override
    public void onCanceled(List<Note> notes) {
        super.onCanceled(notes);
        releaseResources();
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    private void releaseResources() {
        mCursor.close();
    }

}