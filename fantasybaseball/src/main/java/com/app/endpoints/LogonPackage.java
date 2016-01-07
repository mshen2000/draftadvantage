package com.app.endpoints;

public class LogonPackage
{
  private String email;
  private String password;

  /* Constructor needed for serialization operations */
  @SuppressWarnings("unused")
private LogonPackage() {}

  public LogonPackage(String email, String password)
  {
    this.email = email;
    this.password = password;
  }

  public String getEmail(){
    return this.email;
  }

  public String getPassword(){
    return this.password;
  }
}