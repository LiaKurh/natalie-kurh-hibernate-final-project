package com.javarush.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionManagerTest {
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @InjectMocks
    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
    }

    @Test
    @DisplayName("runInTransaction() should commit transaction when code executes successfully")
    void should_CommitTransaction_When_ActionSucceeds() {
        Supplier<String> action = () -> "Success Data";
        String result = transactionManager.runInTransaction(action);
        assertEquals("Success Data", result);
        verify(transaction, times(1)).commit();
        verify(transaction, never()).rollback();
    }

    @Test
    @DisplayName("runInTransaction() should rollback transaction and rethrow exception when action fails")
    void should_RollbackAndRethrowException_When_ActionFails() {
        Supplier<String> failingAction = () -> {
            throw new RuntimeException("Database error!");
        };
        when(transaction.isActive()).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                transactionManager.runInTransaction(failingAction)
        );
        assertEquals("Database error!", exception.getMessage());
        verify(transaction, times(1)).rollback();
    }
}
