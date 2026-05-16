package com.fieldflow.core.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.fieldflow.core.database.dao.BusinessDao;
import com.fieldflow.core.database.dao.BusinessDao_Impl;
import com.fieldflow.core.database.dao.UserDao;
import com.fieldflow.core.database.dao.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FieldFlowDatabase_Impl extends FieldFlowDatabase {
  private volatile UserDao _userDao;

  private volatile BusinessDao _businessDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cached_users` (`id` TEXT NOT NULL, `email` TEXT NOT NULL, `first_name` TEXT NOT NULL, `last_name` TEXT NOT NULL, `company` TEXT NOT NULL, `title` TEXT NOT NULL, `territory` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `businesses` (`leadbeamId` TEXT NOT NULL, `name` TEXT NOT NULL, `lat` REAL NOT NULL, `long` REAL NOT NULL, `latGrid` INTEGER NOT NULL, `longGrid` INTEGER NOT NULL, `category` TEXT NOT NULL, `isChain` INTEGER NOT NULL, `rating` REAL, `overallConfidence` REAL NOT NULL, PRIMARY KEY(`leadbeamId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_businesses_latGrid_longGrid` ON `businesses` (`latGrid`, `longGrid`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_businesses_category` ON `businesses` (`category`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4d8892a1631a2571ca0d207d3a65874d')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cached_users`");
        db.execSQL("DROP TABLE IF EXISTS `businesses`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCachedUsers = new HashMap<String, TableInfo.Column>(7);
        _columnsCachedUsers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedUsers.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedUsers.put("first_name", new TableInfo.Column("first_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedUsers.put("last_name", new TableInfo.Column("last_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedUsers.put("company", new TableInfo.Column("company", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedUsers.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedUsers.put("territory", new TableInfo.Column("territory", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCachedUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCachedUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCachedUsers = new TableInfo("cached_users", _columnsCachedUsers, _foreignKeysCachedUsers, _indicesCachedUsers);
        final TableInfo _existingCachedUsers = TableInfo.read(db, "cached_users");
        if (!_infoCachedUsers.equals(_existingCachedUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "cached_users(com.fieldflow.core.database.entity.UserEntity).\n"
                  + " Expected:\n" + _infoCachedUsers + "\n"
                  + " Found:\n" + _existingCachedUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsBusinesses = new HashMap<String, TableInfo.Column>(10);
        _columnsBusinesses.put("leadbeamId", new TableInfo.Column("leadbeamId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBusinesses.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBusinesses.put("lat", new TableInfo.Column("lat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBusinesses.put("long", new TableInfo.Column("long", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBusinesses.put("latGrid", new TableInfo.Column("latGrid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBusinesses.put("longGrid", new TableInfo.Column("longGrid", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBusinesses.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBusinesses.put("isChain", new TableInfo.Column("isChain", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBusinesses.put("rating", new TableInfo.Column("rating", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBusinesses.put("overallConfidence", new TableInfo.Column("overallConfidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBusinesses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBusinesses = new HashSet<TableInfo.Index>(2);
        _indicesBusinesses.add(new TableInfo.Index("index_businesses_latGrid_longGrid", false, Arrays.asList("latGrid", "longGrid"), Arrays.asList("ASC", "ASC")));
        _indicesBusinesses.add(new TableInfo.Index("index_businesses_category", false, Arrays.asList("category"), Arrays.asList("ASC")));
        final TableInfo _infoBusinesses = new TableInfo("businesses", _columnsBusinesses, _foreignKeysBusinesses, _indicesBusinesses);
        final TableInfo _existingBusinesses = TableInfo.read(db, "businesses");
        if (!_infoBusinesses.equals(_existingBusinesses)) {
          return new RoomOpenHelper.ValidationResult(false, "businesses(com.fieldflow.core.database.entity.BusinessEntity).\n"
                  + " Expected:\n" + _infoBusinesses + "\n"
                  + " Found:\n" + _existingBusinesses);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4d8892a1631a2571ca0d207d3a65874d", "9d4353b2421696b21f96937fe9bb3908");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cached_users","businesses");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `cached_users`");
      _db.execSQL("DELETE FROM `businesses`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BusinessDao.class, BusinessDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public BusinessDao businessDao() {
    if (_businessDao != null) {
      return _businessDao;
    } else {
      synchronized(this) {
        if(_businessDao == null) {
          _businessDao = new BusinessDao_Impl(this);
        }
        return _businessDao;
      }
    }
  }
}
