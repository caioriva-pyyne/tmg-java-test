package com.example.tmgjavatest.service;

public class ConcurrentJDKStackServiceTests extends  StackServiceImplTests {
    @Override
    protected <T> StackService<T> getStackServiceInstance() {
        return new ConcurrentJDKStackService<>();
    }
}
