package com.vitalsync.shared;

public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }
}