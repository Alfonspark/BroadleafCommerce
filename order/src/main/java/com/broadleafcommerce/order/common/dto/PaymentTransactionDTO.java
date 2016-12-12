package com.broadleafcommerce.order.common.dto;

import org.broadleafcommerce.common.api.APIUnwrapper;
import org.broadleafcommerce.common.api.APIWrapper;
import org.broadleafcommerce.common.api.BaseWrapper;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.Data;

@Data
public class PaymentTransactionDTO extends BaseWrapper implements APIWrapper<PaymentTransaction>, APIUnwrapper<PaymentTransaction> {
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("id")
    protected Long id;

    @JsonProperty("parentTransaction")
    protected PaymentTransactionDTO parentTransaction;

    @JsonProperty("amount")
    protected Money amount;

    @JsonProperty("date")
    protected Date date;

    @JsonProperty("successful")
    protected Boolean successful;

    @JsonProperty("additionalFields")
    protected Map<String, String> additionalFields;

    @JsonProperty("isSaveToken")
    protected Boolean isSaveToken;
    
    @Override
    public PaymentTransaction unwrap(HttpServletRequest request, ApplicationContext context) {
        OrderPaymentService paymentService = (OrderPaymentService) context.getBean("blOrderPaymentService");
        PaymentTransaction transaction = paymentService.createTransaction();
        transaction.setId(this.id);
        if (parentTransaction != null) {
            transaction.setParentTransaction(parentTransaction.unwrap(request, context));
        }
        transaction.setAmount(this.amount);
        transaction.setDate(this.date);
        transaction.setSuccess(this.successful);
        transaction.setAdditionalFields(this.additionalFields);
        transaction.setSaveToken(this.isSaveToken);
        return transaction;
    }

    @Override
    public void wrapDetails(PaymentTransaction transaction, HttpServletRequest request) {
        this.id = transaction.getId();
        if (transaction.getParentTransaction() != null) {
            PaymentTransactionDTO parent = (PaymentTransactionDTO) context.getBean(PaymentTransactionDTO.class.getName());
            parent.wrapDetails(transaction.getParentTransaction(), request);
            this.parentTransaction = parent;
        }
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
        this.successful = transaction.getSuccess();
        this.additionalFields = transaction.getAdditionalFields();
        this.isSaveToken = transaction.isSaveToken();
    }

    @Override
    public void wrapSummary(PaymentTransaction transaction, HttpServletRequest request) {
        wrapDetails(transaction, request);
        
    }
}
