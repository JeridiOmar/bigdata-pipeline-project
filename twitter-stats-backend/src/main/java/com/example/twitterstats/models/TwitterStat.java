package com.example.twitterstats.models;

public class TwitterStat {
    private double bitcoinPositive;
    private double bitcoinNegative;
    private double ethereumPositive;
    private double ethereumNegative;

    public double getBitcoinPositive() {
        return bitcoinPositive;
    }

    public void setBitcoinPositive(double bitcoinPositive) {
        this.bitcoinPositive = bitcoinPositive;
    }

    public double getBitcoinNegative() {
        return bitcoinNegative;
    }

    public void setBitcoinNegative(double bitcoinNegative) {
        this.bitcoinNegative = bitcoinNegative;
    }

    public double getEthereumPositive() {
        return ethereumPositive;
    }

    public void setEthereumPositive(double ethereumPositive) {
        this.ethereumPositive = ethereumPositive;
    }

    public double getEthereumNegative() {
        return ethereumNegative;
    }

    public void setEthereumNegative(double ethereumNegative) {
        this.ethereumNegative = ethereumNegative;
    }
}
