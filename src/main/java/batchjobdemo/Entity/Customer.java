package batchjobdemo.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="CUSTOMER_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    
    @Id
    @Column(name="CUSTOMER_ID")
    private int id;
    @Column(name="First_Name")
    private String firstname;
    @Column(name="Last_Name")
    private String lastname;
    @Column(name="EMAIL")
    private String email;
    @Column(name="GENDER")
    private String gender;
    @Column(name="CONTACT")
    private String contactNo;
    @Column(name="COUNTRY")
    private String country;
    @Column(name="DOB")
    private String dob;
    @Column(name="CREDIT")
    private int credit;
}
