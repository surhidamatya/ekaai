package com.lftechnology.unito.testing;

/**
 * Created by Grishma Shrestha <grishmashrestha@lftechnology.com> on 3/16/16.
 */
public class Person {
    private int age;
    private String name;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getAge() {
        System.out.println("get age");
        return age;
    }

    public void setAge(int age) {
        System.out.println("set age");
        this.age = age;
    }

    public String getName() {
        System.out.println("get Name");

        return name;
    }

    public void setName(String name) {
        System.out.println("set Name");
        this.name = name;
    }

    public void  addName(String name) {
        if (this.name == null) {
            this.name = this.name + name;
        }

    }
}
