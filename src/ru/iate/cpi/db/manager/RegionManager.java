package ru.iate.cpi.db.manager;

import android.content.Context;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import ru.iate.cpi.R;
import ru.iate.cpi.db.helper.OrmLiteDatabaseHelper;
import ru.iate.cpi.db.table.Region;
import ru.iate.cpi.service.CpiService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by sanea on 10.05.15.
 */
public class RegionManager {
    private final Context _context;
    private final OrmLiteDatabaseHelper _db;

    public RegionManager(Context context, OrmLiteDatabaseHelper db){
        _context = context;
        _db = db;
    }

    public void FillRegions(){
        String line;
        Region cpiRegion;
        InputStream sourceStream = _context.getResources().openRawResource(R.raw.cpi_regions);
        BufferedReader r = new BufferedReader(new InputStreamReader(sourceStream));

        try {
            Dao<Region,Integer> daoRegions = _db.getRegionDao();
            while ((line = r.readLine()) != null) {
                String[] splittedArray = line.split(";");
                cpiRegion = new Region(splittedArray[1].trim(), splittedArray[0].trim());
                daoRegions.create(cpiRegion);
            }
        }
        catch(SQLException ex){
            Log.d(CpiService.ERROR_PREFIX, ex.getMessage());
        }
        catch (IOException ex){
            Log.d(CpiService.ERROR_PREFIX, ex.getMessage());
        }
    }

    public List<Region> GetRegions(){
        try {
            Dao<Region,Integer> daoRegions = _db.getRegionDao();
            return daoRegions.queryForAll();
        }
        catch(SQLException ex){
            Log.d(CpiService.ERROR_PREFIX, ex.getMessage());
            return null;
        }
    }
}
