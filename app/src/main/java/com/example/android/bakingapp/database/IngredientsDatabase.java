package com.example.android.bakingapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {IngredientsObject.class}, version = 1, exportSchema = false)
public abstract class IngredientsDatabase extends RoomDatabase{
    public abstract IngredientsDao ingredientsDao();
    private static IngredientsDatabase INSTANCE;

    public static IngredientsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (IngredientsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            IngredientsDatabase.class, "ingredients_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
