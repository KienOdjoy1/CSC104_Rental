public abstract class Person {
    private String name;
    private String contactNumber;
    private String email;

    public Person(String name, String contactNumber, String email) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getName() { 
    	return name; 
    }
    
    public String getContactNumber() { 
    	return contactNumber; 
    }
    
    public String getEmail() { 
    	return email; 
    }

    public void setContactNumber(String contactNumber) { 
    	this.contactNumber = contactNumber; 
    }
    
    public void setEmail(String email) { 
    	this.email = email; 
    }
    
    public abstract String getRole();
    
}