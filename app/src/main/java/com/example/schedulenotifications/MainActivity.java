package com.example.schedulenotifications;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.example.schedulenotifications.models.Contact;
import com.example.schedulenotifications.repositories.ContactRepository;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ContactRepository contactRepository;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        contactRepository = new ContactRepository(getApplication());

        // if the app is being explicitly started, refresh notification tasks just in case
        startNotifications();
    }

    public void createContact(View view)
    {
        Intent intent = new Intent(this, ContactEdit.class);
        startActivity(intent);
    }

    public void selectContact(View view)
    {
        Intent intent = new Intent(this, ContactSelect.class);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    private void startNotifications() {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                List<Contact> contactList = contactRepository.getAllContacts();

                for (Contact contact : contactList) {
                    MessageCreator.setMessageTask(context, contact);
                }
                return null;
            }
        }.execute();
    }
}
