package com.amy.stockwatch;

public class Stock implements Comparable< Stock > {
    private String symbol;
    private String name;
    private double price;
    private double priceChange;
    private double percentChange;

    public Stock(String symbol, String name, double price, double priceChange, double percentChange) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.priceChange = priceChange;
        this.percentChange = percentChange;
    }

    public String getSymbol() {
        return this.symbol.toUpperCase();
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public double getPriceChange() {
        return this.priceChange;
    }

    public double getPercentChange() {
        return this.percentChange;
    }

    public void setSymbol(String s) {
        symbol = s;
    }

    public void setName(String n) {
        name = n;
    }

    public void setPrice(double p) {
        price = p;
    }

    public void setPriceChange(double pc) {
        priceChange = pc;
    }

    public void setPercentChange(double perc) {
        percentChange = perc;
    }

    @Override
    public String toString() {
        return String.format("Stock Info: symbol->%s, name->%s, price->%.2f, priceChange->%.2f, percentChange->%.2f",
                symbol, name, price, priceChange, percentChange);
    }

    @Override
    public int compareTo(Stock s) {
        return this.getSymbol().compareTo(s.getSymbol());
    }
}