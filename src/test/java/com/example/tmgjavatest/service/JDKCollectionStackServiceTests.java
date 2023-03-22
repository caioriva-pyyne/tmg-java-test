package com.example.tmgjavatest.service;

public class JDKCollectionStackServiceTests extends  StackServiceImplTests {
    @Override
    protected <T> StackService<T> getStackServiceInstance() {
        return new JDKCollectionStackService<>();
    }
}
