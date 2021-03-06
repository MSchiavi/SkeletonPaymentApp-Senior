package com.imobile3.groovypayments.ui.checkout;

import android.content.Context;

import com.imobile3.groovypayments.concurrent.GroovyExecutors;
import com.imobile3.groovypayments.data.PaymentTypeRepository;
import com.imobile3.groovypayments.data.model.PaymentType;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The ViewModel serves as an async bridge between the View (Activity, Fragment)
 * and our backing data repository (Database).
 */
public class CheckoutViewModel extends ViewModel {

    private PaymentTypeRepository mRepository;

    //Payment amount LiveData objects to be used when editing how much will be paid.
    private MutableLiveData<Long> _paymentAmount = new MutableLiveData<>(0L);
    public LiveData<Long> paymentAmountObservable() {
        return _paymentAmount;
    }

    CheckoutViewModel(PaymentTypeRepository repository) {
        mRepository = repository;
    }

    public LiveData<List<PaymentType>> getPaymentTypes(@NonNull final Context context) {
        // Caller should observe this object for changes. When the data has finished
        // async loading, the observer can react accordingly.
        final MutableLiveData<List<PaymentType>> observable =
                new MutableLiveData<>(new ArrayList<>());

        GroovyExecutors.getInstance().getDiskIo().execute(() -> {
            List<PaymentType> resultSet = mRepository.getPaymentTypes(context);
            observable.postValue(resultSet);
        });

        return observable;
    }

    public void setPaymentAmount(long amount){
        _paymentAmount.postValue(amount);
    }

    public void pushPaymentAmount(long amount){
        long paymentAmount = _paymentAmount.getValue();
        paymentAmount = paymentAmount*10 + amount;
        System.out.println(paymentAmount);
        _paymentAmount.postValue(paymentAmount);
    }

    public void popPaymentAmount(){
        long paymentAmount = _paymentAmount.getValue();
            paymentAmount = paymentAmount / 10;
            _paymentAmount.postValue(paymentAmount);
            System.out.println(paymentAmount);
    }

    public long changeDue(long cartAmount){
        long paymentAmount = _paymentAmount.getValue();
        return paymentAmount - cartAmount;
    }


}
