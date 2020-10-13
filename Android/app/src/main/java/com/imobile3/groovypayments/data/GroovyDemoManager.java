package com.imobile3.groovypayments.data;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

import com.imobile3.groovypayments.MainApplication;
import com.imobile3.groovypayments.data.entities.CartEntity;
import com.imobile3.groovypayments.data.entities.CartProductEntity;
import com.imobile3.groovypayments.data.entities.ProductEntity;
import com.imobile3.groovypayments.data.entities.ProductTaxJunctionEntity;
import com.imobile3.groovypayments.data.entities.TaxEntity;
import com.imobile3.groovypayments.data.enums.GroovyColor;
import com.imobile3.groovypayments.data.enums.GroovyIcon;
import com.imobile3.groovypayments.data.utils.CartBuilder;
import com.imobile3.groovypayments.data.utils.CartProductBuilder;
import com.imobile3.groovypayments.data.utils.ProductBuilder;
import com.imobile3.groovypayments.data.utils.ProductTaxJunctionBuilder;
import com.imobile3.groovypayments.data.utils.TaxBuilder;

import androidx.annotation.NonNull;
import androidx.room.Database;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class GroovyDemoManager {

    private static final String TAG = GroovyDemoManager.class.getSimpleName();

    //region Singleton Implementation

    private static GroovyDemoManager sInstance;

    private GroovyDemoManager() {
    }

    @NonNull
    public static synchronized GroovyDemoManager getInstance() {
        if (sInstance == null) {
            sInstance = new GroovyDemoManager();
        }
        return sInstance;
    }

    //endregion

    public interface ResetDatabaseCallback {

        void onDatabaseReset();
    }

    /**
     * Delete the current database instance (potentially dangerous operation!)
     * and populate a new instance with fresh demo data.
     */
    public void resetDatabase(
            @NonNull final ResetDatabaseCallback callback) {
        new ResetDatabaseTask(MainApplication.getInstance(), callback)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class ResetDatabaseTask extends AsyncTask<Void, Void, Void> {

        @NonNull
        private final Context mContext;
        @NonNull
        private final ResetDatabaseCallback mCallback;

        private ResetDatabaseTask(
                @NonNull final Context context,
                @NonNull final ResetDatabaseCallback callback) {
            mContext = context;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Blow away any existing database instance.
            DatabaseHelper.getInstance().eraseDatabase(mContext);

            // Initialize a new database instance.
            DatabaseHelper.getInstance().init(mContext);

            new InventoryWorker().run();

            new OrderHistoryWorker().run();

            // All done!
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mCallback.onDatabaseReset();
        }
    }

    private class InventoryWorker implements Runnable {

        @Override
        public void run() {

            List<ProductEntity> productEntities = new ArrayList<>();

            List<TaxEntity> taxEntities = new ArrayList<>();

            List<ProductTaxJunctionEntity> productTaxJunctionEntities = new ArrayList<>();

            productEntities.add(ProductBuilder.build(101L,
                    "Tasty Chicken Sandwich",
                    "Chicken, lettuce, tomato and mayo",
                    750L, 200L,
                    GroovyIcon.Sandwich, GroovyColor.Yellow));

            productEntities.add(ProductBuilder.build(102L,
                    "Delicous Spicy Wings",
                    "Chicken Wings, Cajun, Blue Cheese",
                    600L, 100L,
                    GroovyIcon.Sandwich, GroovyColor.Orange));

            productEntities.add(ProductBuilder.build(103L,
                    "Tasty Roast Beef Sandwich",
                    "Beef, horseradish, Salty roll",
                    750L, 200L,
                    GroovyIcon.Sandwich, GroovyColor.Yellow));

            productEntities.add(ProductBuilder.build(104L,
                    "Coffee",
                    "Hot Coffee",
                    100L, 100L,
                    GroovyIcon.CoffeeMug, GroovyColor.Gray));

            taxEntities.add(TaxBuilder.build(101L,
                    "Chicken", "0.08"));
            taxEntities.add(TaxBuilder.build(102L,
                    "Beef", "0.09"));
            taxEntities.add(TaxBuilder.build(103L,
                    "Drink", "0.06"));

            //Chicken Sandwich with Chicken tax
            productTaxJunctionEntities.add(ProductTaxJunctionBuilder.build(101L, 101L));
            // Chicken wings with Chicken tax
            productTaxJunctionEntities.add(ProductTaxJunctionBuilder.build(102L, 101L));
            // Beef Sandwich with Beef tax
            productTaxJunctionEntities.add(ProductTaxJunctionBuilder.build(103L, 102L));
            // Coffee with Drink Tax
            productTaxJunctionEntities.add(ProductTaxJunctionBuilder.build(104, 103));

            // Insert entities into database instance.
            DatabaseHelper.getInstance().getDatabase().getProductDao()
                    .insertProducts(
                            productEntities.toArray(new ProductEntity[0]));

            DatabaseHelper.getInstance().getDatabase().getTaxDao().insertTaxes(
                    taxEntities.toArray(new TaxEntity[0]));

            DatabaseHelper.getInstance().getDatabase().getProductTaxJunctionDao().insertProductTaxJunctions(
                    productTaxJunctionEntities.toArray(new ProductTaxJunctionEntity[0]));

        }
    }

    private class OrderHistoryWorker implements Runnable {

        @Override
        public void run() {
            List<CartEntity> cartEntities = new ArrayList<>();

            List<CartProductEntity> cartProductEntities = new ArrayList<>();

            //TODO When restarting the app this database entry is not being pulled with a description

            cartEntities.add(CartBuilder.build(101L, new Date(),1000L,200L,1653L));
            cartProductEntities.add(CartProductBuilder.build(101L,101L,"Coffee, Delicous Spicy Wings, Tasty Chicken Sandwich",1653L,1));

            DatabaseHelper.getInstance().getDatabase().getCartDao().insertCarts(
                    cartEntities.toArray(new CartEntity[0]));

            DatabaseHelper.getInstance().getDatabase().getCartProductDao().insertCartProducts(
                    cartProductEntities.toArray(new CartProductEntity[0]));

        }
    }
}
