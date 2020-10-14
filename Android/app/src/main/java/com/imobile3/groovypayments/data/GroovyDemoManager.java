package com.imobile3.groovypayments.data;

import android.content.Context;
import android.os.AsyncTask;

import com.imobile3.groovypayments.MainApplication;
import com.imobile3.groovypayments.calculation.CartCalculator;
import com.imobile3.groovypayments.data.entities.CartEntity;
import com.imobile3.groovypayments.data.entities.CartProductEntity;
import com.imobile3.groovypayments.data.entities.CartTaxEntity;
import com.imobile3.groovypayments.data.entities.ProductEntity;
import com.imobile3.groovypayments.data.entities.ProductTaxJunctionEntity;
import com.imobile3.groovypayments.data.entities.TaxEntity;
import com.imobile3.groovypayments.data.entities.UserEntity;
import com.imobile3.groovypayments.data.model.Cart;

import androidx.annotation.NonNull;
import java.util.ArrayList;
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

            new UserWorker().run();

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

            // Found testData in testRepository
            List<ProductEntity> productEntities = TestDataRepository.getInstance().getProducts(TestDataRepository.Environment.GroovyDemo);

            List<TaxEntity> taxEntities = TestDataRepository.getInstance().getTaxes(TestDataRepository.Environment.GroovyDemo);

            List<ProductTaxJunctionEntity> productTaxJunctionEntities = new ArrayList<>();

            for (int i = 0; i < productEntities.size(); i++) {
                productTaxJunctionEntities.addAll(TestDataRepository.getInstance().getProductTaxJunctions(productEntities.get(i), taxEntities));
            }
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
            List<CartEntity> cartEntities = TestDataRepository.getInstance().getCarts(TestDataRepository.Environment.GroovyDemo);
            List<Cart> cartResults = new ArrayList<>();

            List<CartProductEntity> cartProductEntities = new ArrayList<>();
            List<CartTaxEntity> cartTaxEntities = new ArrayList<>();

            for(int i = 0; i < cartEntities.size(); i++){
                Cart cart = new Cart(cartEntities.get(i));
                cart.setProducts(TestDataRepository.getInstance().getCartProducts(TestDataRepository.Environment.GroovyDemo,cartEntities.get(i)));
                cart.setTaxes(TestDataRepository.getInstance().getCartTaxes(TestDataRepository.Environment.GroovyDemo,cartEntities.get(i)));


                new CartCalculator(cart).calculate();
                cartResults.add(cart);


                cartProductEntities.addAll(cart.getProducts());
                cartTaxEntities.addAll(cart.getTaxes());
            }

            //Need to remove the old CartEntities and replace with the totals calculated.
            cartEntities.clear();
            cartEntities.addAll(cartResults);

            DatabaseHelper.getInstance().getDatabase().getCartDao().insertCarts(
                    cartEntities.toArray(new CartEntity[0]));

            DatabaseHelper.getInstance().getDatabase().getCartProductDao().insertCartProducts(
                    cartProductEntities.toArray(new CartProductEntity[0]));

            DatabaseHelper.getInstance().getDatabase().getCartTaxDao().insertCartTaxes(
                    cartTaxEntities.toArray(new CartTaxEntity[0]));

        }
    }

    private class UserWorker implements Runnable {

        @Override
        public void run() {
            List<UserEntity> userEntities = TestDataRepository.getInstance().getUsers(TestDataRepository.Environment.GroovyDemo);

            DatabaseHelper.getInstance().getDatabase().getUserDao().insertUsers(
                    userEntities.toArray(new UserEntity[0]));
        }
    }
}
