package org.example.springwithdistributedlock.common.exception;

public class ForceException extends RuntimeException{

    public ForceException() {
        super("자식 메서드에서 업데이트 커밋 안됨");
    }
}
