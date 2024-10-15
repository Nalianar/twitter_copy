package org.example.exception

class ResourceNotFoundException extends RuntimeException{

    ResourceNotFoundException(){
        super()
    }

    ResourceNotFoundException(String message){
        super(message)
    }
}
