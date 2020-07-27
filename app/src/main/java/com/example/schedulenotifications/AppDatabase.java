package com.example.schedulenotifications;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.schedulenotifications.daos.ContactDao;
import com.example.schedulenotifications.models.Contact;

@TypeConverters({Converters.class})
@Database(entities = {Contact.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String TABLE_NAME_CONTACTS = "Contact";
    public abstract ContactDao contactDao();
}
