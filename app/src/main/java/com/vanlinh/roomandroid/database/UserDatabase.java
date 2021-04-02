package com.vanlinh.roomandroid.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.vanlinh.roomandroid.User;

@Database(entities = {User.class},version = 2)
public abstract class UserDatabase extends RoomDatabase {

    // alter table ten_bang add ten_cot dinh_nghia_cot  -> thêm 1 cột vào bảng
    // alter table ten_bang add cot1 dinh_nghia_cot1, cot2 dinh_nghia_cot2,...cot_n dinh_nghia_cotn -> thêm nhiều cột vào bảng
    // alter table ten_bang alter column ten_cot kieucot -> chỉnh sửa kiểu dữ liệu trong một bảng
    // alter table ten_bang drop column ten_cot -> xóa cột trong một bảng

    static Migration migration_from_1_to_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("alter table user add column year TEXT");
        }
    };

    private static final String DATABASE_NAME = "user.db";
    private static UserDatabase instance;

    public  static synchronized UserDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),UserDatabase.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(migration_from_1_to_2)
                    .build();
        }

        return instance;
    }

    public abstract UserDao userDao();
}
