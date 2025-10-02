package org.ideaprojects.ecommerce.exceptions;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException{

    private String resourceName;
    private String field;
    private String fieldName;
    private Long id;
    public ResourceNotFoundException(String resourceName,String field,Long id){
        super(String.format("%s not found with %s : %d",resourceName,field,id));
        this.resourceName=resourceName;
        this.field=field;
        this.id=id;

    }
    public ResourceNotFoundException(String resourceName,String field,String fieldName){
        super(String.format("%s not found with %s : %s",resourceName,fieldName,fieldName));
        this.resourceName=resourceName;
        this.field=field;
        this.fieldName=fieldName;

    }
}
