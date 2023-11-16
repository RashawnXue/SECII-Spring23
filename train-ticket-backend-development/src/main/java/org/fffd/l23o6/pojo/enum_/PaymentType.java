package org.fffd.l23o6.pojo.enum_;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentType {
    @JsonProperty("扫码支付") SCAN_QRCODE("扫码支付"), @JsonProperty("余额支付") WALLET_PAY("余额支付");

    private String text;

    PaymentType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
