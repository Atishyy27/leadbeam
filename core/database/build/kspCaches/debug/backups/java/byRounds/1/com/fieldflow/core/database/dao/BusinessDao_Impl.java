package com.fieldflow.core.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fieldflow.core.database.entity.BusinessEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BusinessDao_Impl implements BusinessDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BusinessEntity> __insertionAdapterOfBusinessEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearBusinesses;

  public BusinessDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBusinessEntity = new EntityInsertionAdapter<BusinessEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `businesses` (`leadbeamId`,`name`,`lat`,`long`,`latGrid`,`longGrid`,`category`,`isChain`,`rating`,`overallConfidence`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BusinessEntity entity) {
        statement.bindString(1, entity.getLeadbeamId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getLat());
        statement.bindDouble(4, entity.getLong());
        statement.bindLong(5, entity.getLatGrid());
        statement.bindLong(6, entity.getLongGrid());
        statement.bindString(7, entity.getCategory());
        final int _tmp = entity.isChain() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getRating() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getRating());
        }
        statement.bindDouble(10, entity.getOverallConfidence());
      }
    };
    this.__preparedStmtOfClearBusinesses = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM businesses";
        return _query;
      }
    };
  }

  @Override
  public Object insertBusinesses(final List<BusinessEntity> businesses,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBusinessEntity.insert(businesses);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearBusinesses(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearBusinesses.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearBusinesses.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getBusinessesInGrid(final int minLatGrid, final int maxLatGrid,
      final int minLongGrid, final int maxLongGrid,
      final Continuation<? super List<BusinessEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM businesses \n"
            + "        WHERE latGrid BETWEEN ? AND ? \n"
            + "        AND longGrid BETWEEN ? AND ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, minLatGrid);
    _argIndex = 2;
    _statement.bindLong(_argIndex, maxLatGrid);
    _argIndex = 3;
    _statement.bindLong(_argIndex, minLongGrid);
    _argIndex = 4;
    _statement.bindLong(_argIndex, maxLongGrid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BusinessEntity>>() {
      @Override
      @NonNull
      public List<BusinessEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLeadbeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "leadbeamId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLong = CursorUtil.getColumnIndexOrThrow(_cursor, "long");
          final int _cursorIndexOfLatGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "latGrid");
          final int _cursorIndexOfLongGrid = CursorUtil.getColumnIndexOrThrow(_cursor, "longGrid");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsChain = CursorUtil.getColumnIndexOrThrow(_cursor, "isChain");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfOverallConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "overallConfidence");
          final List<BusinessEntity> _result = new ArrayList<BusinessEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BusinessEntity _item;
            final String _tmpLeadbeamId;
            _tmpLeadbeamId = _cursor.getString(_cursorIndexOfLeadbeamId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLong;
            _tmpLong = _cursor.getDouble(_cursorIndexOfLong);
            final int _tmpLatGrid;
            _tmpLatGrid = _cursor.getInt(_cursorIndexOfLatGrid);
            final int _tmpLongGrid;
            _tmpLongGrid = _cursor.getInt(_cursorIndexOfLongGrid);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsChain;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsChain);
            _tmpIsChain = _tmp != 0;
            final Double _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            }
            final double _tmpOverallConfidence;
            _tmpOverallConfidence = _cursor.getDouble(_cursorIndexOfOverallConfidence);
            _item = new BusinessEntity(_tmpLeadbeamId,_tmpName,_tmpLat,_tmpLong,_tmpLatGrid,_tmpLongGrid,_tmpCategory,_tmpIsChain,_tmpRating,_tmpOverallConfidence);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
