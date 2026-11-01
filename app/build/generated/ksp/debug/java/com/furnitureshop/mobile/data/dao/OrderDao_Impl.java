package com.furnitureshop.mobile.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.furnitureshop.mobile.data.entity.OrderEntity;
import com.furnitureshop.mobile.data.entity.OrderItemEntity;
import java.lang.Class;
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
public final class OrderDao_Impl implements OrderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OrderEntity> __insertionAdapterOfOrderEntity;

  private final EntityInsertionAdapter<OrderItemEntity> __insertionAdapterOfOrderItemEntity;

  public OrderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOrderEntity = new EntityInsertionAdapter<OrderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `orders` (`orderId`,`userEmail`,`subtotal`,`discount`,`gst`,`total`,`placedAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OrderEntity entity) {
        statement.bindString(1, entity.getOrderId());
        statement.bindString(2, entity.getUserEmail());
        statement.bindDouble(3, entity.getSubtotal());
        statement.bindDouble(4, entity.getDiscount());
        statement.bindDouble(5, entity.getGst());
        statement.bindDouble(6, entity.getTotal());
        statement.bindLong(7, entity.getPlacedAt());
      }
    };
    this.__insertionAdapterOfOrderItemEntity = new EntityInsertionAdapter<OrderItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `order_items` (`id`,`orderId`,`productName`,`unitPrice`,`quantity`,`hostEmail`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OrderItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getOrderId());
        statement.bindString(3, entity.getProductName());
        statement.bindDouble(4, entity.getUnitPrice());
        statement.bindLong(5, entity.getQuantity());
        statement.bindString(6, entity.getHostEmail());
      }
    };
  }

  @Override
  public Object insertOrder(final OrderEntity order, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOrderEntity.insert(order);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItems(final List<OrderItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOrderItemEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getOrdersForUser(final String email,
      final Continuation<? super List<OrderEntity>> $completion) {
    final String _sql = "SELECT * FROM orders WHERE userEmail = ? ORDER BY placedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, email);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<OrderEntity>>() {
      @Override
      @NonNull
      public List<OrderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "orderId");
          final int _cursorIndexOfUserEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "userEmail");
          final int _cursorIndexOfSubtotal = CursorUtil.getColumnIndexOrThrow(_cursor, "subtotal");
          final int _cursorIndexOfDiscount = CursorUtil.getColumnIndexOrThrow(_cursor, "discount");
          final int _cursorIndexOfGst = CursorUtil.getColumnIndexOrThrow(_cursor, "gst");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfPlacedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "placedAt");
          final List<OrderEntity> _result = new ArrayList<OrderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OrderEntity _item;
            final String _tmpOrderId;
            _tmpOrderId = _cursor.getString(_cursorIndexOfOrderId);
            final String _tmpUserEmail;
            _tmpUserEmail = _cursor.getString(_cursorIndexOfUserEmail);
            final double _tmpSubtotal;
            _tmpSubtotal = _cursor.getDouble(_cursorIndexOfSubtotal);
            final double _tmpDiscount;
            _tmpDiscount = _cursor.getDouble(_cursorIndexOfDiscount);
            final double _tmpGst;
            _tmpGst = _cursor.getDouble(_cursorIndexOfGst);
            final double _tmpTotal;
            _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            final long _tmpPlacedAt;
            _tmpPlacedAt = _cursor.getLong(_cursorIndexOfPlacedAt);
            _item = new OrderEntity(_tmpOrderId,_tmpUserEmail,_tmpSubtotal,_tmpDiscount,_tmpGst,_tmpTotal,_tmpPlacedAt);
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

  @Override
  public Object getSalesForHost(final String email,
      final Continuation<? super List<HostSale>> $completion) {
    final String _sql = "SELECT oi.orderId AS orderId, oi.productName AS productName, oi.unitPrice AS unitPrice, oi.quantity AS quantity, o.placedAt AS placedAt FROM order_items oi INNER JOIN orders o ON o.orderId = oi.orderId WHERE oi.hostEmail = ? ORDER BY o.placedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, email);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HostSale>>() {
      @Override
      @NonNull
      public List<HostSale> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfOrderId = 0;
          final int _cursorIndexOfProductName = 1;
          final int _cursorIndexOfUnitPrice = 2;
          final int _cursorIndexOfQuantity = 3;
          final int _cursorIndexOfPlacedAt = 4;
          final List<HostSale> _result = new ArrayList<HostSale>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HostSale _item;
            final String _tmpOrderId;
            _tmpOrderId = _cursor.getString(_cursorIndexOfOrderId);
            final String _tmpProductName;
            _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            final double _tmpUnitPrice;
            _tmpUnitPrice = _cursor.getDouble(_cursorIndexOfUnitPrice);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final long _tmpPlacedAt;
            _tmpPlacedAt = _cursor.getLong(_cursorIndexOfPlacedAt);
            _item = new HostSale(_tmpOrderId,_tmpProductName,_tmpUnitPrice,_tmpQuantity,_tmpPlacedAt);
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

  @Override
  public Object getItemsForOrder(final String orderId,
      final Continuation<? super List<OrderItemEntity>> $completion) {
    final String _sql = "SELECT * FROM order_items WHERE orderId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, orderId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<OrderItemEntity>>() {
      @Override
      @NonNull
      public List<OrderItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "orderId");
          final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "productName");
          final int _cursorIndexOfUnitPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "unitPrice");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfHostEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "hostEmail");
          final List<OrderItemEntity> _result = new ArrayList<OrderItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OrderItemEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpOrderId;
            _tmpOrderId = _cursor.getString(_cursorIndexOfOrderId);
            final String _tmpProductName;
            _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            final double _tmpUnitPrice;
            _tmpUnitPrice = _cursor.getDouble(_cursorIndexOfUnitPrice);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final String _tmpHostEmail;
            _tmpHostEmail = _cursor.getString(_cursorIndexOfHostEmail);
            _item = new OrderItemEntity(_tmpId,_tmpOrderId,_tmpProductName,_tmpUnitPrice,_tmpQuantity,_tmpHostEmail);
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
