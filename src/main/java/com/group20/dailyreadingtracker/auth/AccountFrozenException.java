package com.group20.dailyreadingtracker.auth;

/*  
 * @author Yongtai Li
*/

public class AccountFrozenException extends RuntimeException{
    public AccountFrozenException(String message){
        super(message);
    }
}
