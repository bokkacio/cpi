package ru.iate.cpi.db.table;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by sanea on 04.05.15.
 */

@DatabaseTable(tableName = "cpi_store")
public class Store {

    public final static String STORE_ID_FIELD = "store_id";
    public final static String STORE_TITLE_FIELD = "store_title";

    public Store(){
        //needed by ormlite
    }

    public Store(String title){
        this.Title = title;
    }

    @DatabaseField(generatedId = true, columnName = STORE_ID_FIELD)
    private int Id;

    @DatabaseField(canBeNull = false, unique = true, dataType = DataType.STRING, columnName = STORE_TITLE_FIELD)
    private String Title;

    public int GetId(){
        return this.Id;
    }

    public String GetTitle(){
        return this.Title;
    }
}
