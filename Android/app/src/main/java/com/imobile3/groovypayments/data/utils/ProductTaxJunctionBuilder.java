package com.imobile3.groovypayments.data.utils;

import androidx.annotation.NonNull;

import com.imobile3.groovypayments.data.entities.ProductTaxJunctionEntity;

public final class ProductTaxJunctionBuilder {

    private ProductTaxJunctionBuilder() {
    }

    @NonNull
    public static ProductTaxJunctionEntity build(
        long productId,
        long taxId
    ) {
        ProductTaxJunctionEntity result = new ProductTaxJunctionEntity();
        result.setProductId(productId);
        result.setTaxId(taxId);
        return result;
    }
}
