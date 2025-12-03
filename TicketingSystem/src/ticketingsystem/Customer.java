/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ticketingsystem;

/**
 *
 * @author Cason Soh
 */
public class Customer {

    private String name;
    private String password;
    private int id;
    private static int assignid = 0001;

    public Customer() {
        assignid++;
    }

    public Customer(String name, String password) {
        this.name = name;
        this.password = password;
        id = assignid;
        assignid++;
    }

    public String getname() {
        return name;
    }

    public String getpassword() {
        return password;
    }
    
    public int getId(){
        return id;
    }

    public void setname(String name) {
        this.name = name;
    }

    public void setpassword(String password) {
        this.password = password;
    }

    public boolean cusLogin(String name, String password) {
        if (name.equals(this.name) && password.equals(this.password)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean cusReg(String name) {
        if (name.equals(this.name)) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return name + " | " + password;
    }
}
