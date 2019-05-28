package org.mongodb.sample;

import java.io.Serializable;

public class Character implements Serializable {

	private static final long serialVersionUID = -3109056238114978781L;

	private String name;
	
    private Integer age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}


}
