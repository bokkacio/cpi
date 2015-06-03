package ru.iate.cpi.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import de.greenrobot.event.EventBus;
import ru.iate.cpi.db.DatabaseFactory;
import ru.iate.cpi.db.manager.*;
import ru.iate.cpi.db.table.Category;
import ru.iate.cpi.db.table.Region;
import ru.iate.cpi.db.table.Settings;
import ru.iate.cpi.event.*;
import ru.iate.cpi.ui.LogTags;

/**
 * Created by sanea on 04.05.15.
 */
public class CpiService extends Service {
    private Context _context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        DatabaseFactory.Release();
    }

    //init database
    public void onEventBackgroundThread(InitDatabaseEvent event){
        try {
            DatabaseFactory.Set(getApplicationContext());
            RegionManager regionManager = new RegionManager(_context, DatabaseFactory.Get());
            CategoryManager categoryManager = new CategoryManager(_context, DatabaseFactory.Get());
            QuantityManager quantityManager = new QuantityManager(_context, DatabaseFactory.Get());

            //fill regions only once
            if(regionManager.GetRegions().isEmpty())
                regionManager.FillRegions();

            //fill categories only once
            if(categoryManager.GetCategories().isEmpty())
                categoryManager.FillCategories();

            //fill quantities only once
            if(quantityManager.GetQuantities().isEmpty())
                quantityManager.FillQuantities();
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - InitDatabaseEvent" + ex.getMessage());
        }
    }

    //extract regions
    public void onEventBackgroundThread(GetRegionsEvent event){
        try {

            RegionManager manager = new RegionManager(_context, DatabaseFactory.Get());
            EventBus.getDefault().post(new RegionsSourceEvent(manager.GetRegions()));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - GetRegionsEvent" + ex.getMessage());
        }
    }

    //extract settings
    public void onEventBackgroundThread(GetSettingsEvent event){
        try {

            SettingsManager manager = new SettingsManager(DatabaseFactory.Get());
            RegionManager regionManager = new RegionManager(_context, DatabaseFactory.Get());

            Settings settings = manager.GetSettingsInfo();
            Region region = settings == null ? null : regionManager.GetRegion(settings.GetRegionId());
            EventBus.getDefault().post(new SettingsSourceEvent(settings, region));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - GetSettingsEvent" + ex.getMessage());
        }
    }

    //add settings
    public void onEventBackgroundThread(AddSettingEvent event){
        try {

            SettingsManager manager = new SettingsManager(DatabaseFactory.Get());
            RegionManager regionManager = new RegionManager(_context, DatabaseFactory.Get());
            manager.SetSettingsInfo(event.settings);

            Settings settings = manager.GetSettingsInfo();
            Region region = settings == null ? null : regionManager.GetRegion(settings.GetRegionId());
            EventBus.getDefault().post(new SettingsSourceEvent(settings, region));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - AddSettingEvent" + ex.getMessage());
        }
    }

    //extract stores
    public void onEventBackgroundThread(GetStoresEvent event){
        try {

            StoreManager manager = new StoreManager(DatabaseFactory.Get());
            EventBus.getDefault().post(new StoresSourceEvent(manager.GetStores()));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - GetStoresEvent" + ex.getMessage());
        }
    }

    //add store
    public void onEventBackgroundThread(AddStoreEvent event){
        try {

            StoreManager manager = new StoreManager(DatabaseFactory.Get());
            manager.AddStore(event.newStore);
            EventBus.getDefault().post(new StoresSourceEvent(manager.GetStores()));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - AddStoreEvent" + ex.getMessage());
        }
    }

    //delete store
    public void onEventBackgroundThread(DeleteStoreEvent event){
        try {

            StoreManager manager = new StoreManager(DatabaseFactory.Get());
            manager.DeleteStore(event.storeId);
            EventBus.getDefault().post(new StoresSourceEvent(manager.GetStores()));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - DeleteStoreEvent" + ex.getMessage());
        }
    }

    //extract categories
    public void onEventBackgroundThread(GetCategoriesEvent event){
        try {

            CategoryManager manager = new CategoryManager(_context, DatabaseFactory.Get());
            EventBus.getDefault().post(new CategoriesSourceEvent(manager.GetCategories()));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - GetCategoriesEvent" + ex.getMessage());
        }
    }

    //update category
    public void onEventBackgroundThread(EditCategoryEvent event){
        try {

            CategoryManager manager = new CategoryManager(_context, DatabaseFactory.Get());
            manager.UpdateCategory(event.categoryId, event.weight);
            EventBus.getDefault().post(new CategoriesSourceEvent(manager.GetCategories()));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - EditCategoryEvent" + ex.getMessage());
        }
    }

    //extract products and categories
    public void onEventBackgroundThread(GetProductsAndCategoriesEvent event){
        try {

            CategoryManager categoryManager = new CategoryManager(_context, DatabaseFactory.Get());
            ProductManager productManager = new ProductManager(DatabaseFactory.Get());
            EventBus.getDefault().post(new ProductsAndCategoriesSourceEvent(productManager.GetProducts(), categoryManager.GetCategories()));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - GetProductsAndCategoriesEvent" + ex.getMessage());
        }
    }

    //add product
    public void onEventBackgroundThread(AddProductEvent event){
        try {

            CategoryManager categoryManager = new CategoryManager(_context, DatabaseFactory.Get());
            ProductManager productManager = new ProductManager(DatabaseFactory.Get());
            productManager.AddProduct(event.ProductTitle, event.CategoryId);
            EventBus.getDefault().post(new ProductsAndCategoriesSourceEvent(productManager.GetProducts(), categoryManager.GetCategories()));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - AddProductEvent" + ex.getMessage());
        }
    }

    //delete product
    public void onEventBackgroundThread(DeleteProductEvent event){
        try {

            CategoryManager categoryManager = new CategoryManager(_context, DatabaseFactory.Get());
            ProductManager productManager = new ProductManager(DatabaseFactory.Get());
            productManager.DeleteProduct(event.ProductId);
            EventBus.getDefault().post(new ProductsAndCategoriesSourceEvent(productManager.GetProducts(), categoryManager.GetCategories()));
        }
        catch (Exception ex){
            Log.d(LogTags.ERROR_PREFIX, "CpiService - DeleteProductEvent" + ex.getMessage());
        }
    }
}
