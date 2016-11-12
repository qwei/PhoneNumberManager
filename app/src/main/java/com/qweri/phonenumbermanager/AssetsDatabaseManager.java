package com.qweri.phonenumbermanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AssetsDatabaseManager {

	private static String tag = "AssetsDatabase"; // for LogCat  
    private static String databasepath = "/data/data/%s/databases"; // %s is packageName  
    private static final String FILE_NAME = "callmark.db";
      
      
//    // A mapping from assets database file to SQLiteDatabase object  
//    private Map<String, SQLiteDatabase> databases = new HashMap()<String, SQLiteDatabase>();  
     private SQLiteDatabase database = null;
    // Context of application  
    private Context context = null;  
      
    // Singleton Pattern  
    private static AssetsDatabaseManager mInstance = null;  
      
    /** 
     * Initialize AssetsDatabaseManager 
     * @param context, context of application 
     */  
    public static synchronized AssetsDatabaseManager initManager(Context context){  
        if(mInstance == null){  
            mInstance = new AssetsDatabaseManager(context);  
        }  
        return mInstance;
    }  
      
      
    private AssetsDatabaseManager(Context context){  
        this.context = context;  
    }  
      
    /** 
     * Get a assets database, if this database is opened this method is only return a copy of the opened database 
     * @param dbfile, the assets file which will be opened for a database 
     * @return, if success it return a SQLiteDatabase object else return null 
     */  
    public SQLiteDatabase getDatabase() {  
        if(database != null && database.isOpen()){  
            return database;  
        }  
        if(context==null)  
            return null; 
        
        database = null;
          
        Log.i(tag, String.format("Create database %s", FILE_NAME));  
        String spath = getDatabaseFilepath();  
        String sfile = getDatabaseFile(FILE_NAME);  
          
        File file = new File(sfile);  
        SharedPreferences dbs = context.getSharedPreferences(AssetsDatabaseManager.class.toString(), 0);  
        boolean flag = dbs.getBoolean(FILE_NAME, false); // Get Database file flag, if true means this database file was copied and valid  
        if(!flag || !file.exists()){  
            file = new File(spath);  
            if(!file.exists() && !file.mkdirs()){  
                Log.i(tag, "Create \""+spath+"\" fail!");  
                return null;  
            }  
            if(!copyAssetsToFilesystem(FILE_NAME, sfile)){  
                Log.i(tag, String.format("Copy %s to %s fail!", FILE_NAME, sfile));  
                return null;  
            }  
              
            dbs.edit().putBoolean(FILE_NAME, true).commit();  
        }  
          
        database = SQLiteDatabase.openDatabase(sfile, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);  
        return database;  
    }  
      
    private String getDatabaseFilepath(){  
        return String.format(databasepath, context.getApplicationInfo().packageName);  
    }  
      
    private String getDatabaseFile(String dbfile){  
        return getDatabaseFilepath()+"/"+dbfile;  
    }  
      
    private boolean copyAssetsToFilesystem(String assetsSrc, String des){  
        Log.i(tag, "Copy "+assetsSrc+" to "+des);  
        InputStream istream = null;  
        OutputStream ostream = null;  
        try{  
            AssetManager am = context.getAssets();  
            istream = am.open(assetsSrc);  
            ostream = new FileOutputStream(des);  
            byte[] buffer = new byte[1024];  
            int length;  
            while ((length = istream.read(buffer))>0){  
                ostream.write(buffer, 0, length);  
            }  
            istream.close();  
            ostream.close();  
        }  
        catch(Exception e){  
            e.printStackTrace();  
            try{  
                if(istream!=null)  
                    istream.close();  
                if(ostream!=null)  
                    ostream.close();  
            }  
            catch(Exception ee){  
                ee.printStackTrace();  
            }  
            return false;  
        }  
        return true;  
    }  
      
    /** 
     * Close assets database 
     * @param dbfile, the assets file which will be closed soon 
     * @return, the status of this operating 
     */  
    public boolean closeDatabase(){  
    	try {
    		database.close();
    	} catch(Exception e) {
    		
    	}
    	  
        return true;  
    }  
      
//    /** 
//     * Close all assets database 
//     */  
//    static public void closeAllDatabase(){  
//        Log.i(tag, "closeAllDatabase");  
//        if(mInstance != null){  
//            for(int i=0; i<mInstance.databases.size(); ++i){  
//                if(mInstance.databases.get(i)!=null){  
//                    mInstance.databases.get(i).close();  
//                }  
//            }  
//            mInstance.databases.clear();  
//        }  
//    }  
}
