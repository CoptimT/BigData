package com.zxw.auto.elasticsearch.bean;

public class Car {
	private String factory;
	private String brand;
	private String series;
	
	public Car() {
		super();
	}
	public Car(String factory, String brand, String series) {
		super();
		this.factory = factory;
		this.brand = brand;
		this.series = series;
	}
	
	public String getFactory() {
		return factory;
	}
	public void setFactory(String factory) {
		this.factory = factory;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	@Override
	public String toString() {
		return "Car(factory="+factory+",brand="+brand+",series="+series+")";
	}
}
