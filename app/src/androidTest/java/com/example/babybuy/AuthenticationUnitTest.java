package com.example.babybuy;

import com.example.babybuy.repository.UserRespositoryImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthenticationUnitTest {

    @Mock
    private FirebaseAuth mockAuth;

    @Mock
    private Task<AuthResult> mockTask;

    private UserRespositoryImpl userRepository;

    @Captor
    private ArgumentCaptor<OnCompleteListener<AuthResult>> captor;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this); // Initializes mocks
        userRepository = new UserRespositoryImpl(mockAuth);
    }

    @Test
    public void testRegister_Successful() {
        String email = "kushal@gmail.com";
        String password = "12345678";
        String[] expectedResult = {"Initial Value"}; // Use an array to mimic mutable reference

        // Mocking task to simulate successful registration
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockTask);

        // Define a callback that updates the expectedResult
//        UserRespositoryImpl.Callback callback = (success, message) -> {
//            expectedResult[0] = message != null ? message : "Callback message is null";
//        };

//        // Call the function under test
//        LoginActivity.signup_now_text(email, password, callback);

        // Capture the OnCompleteListener
        verify(mockAuth).createUserWithEmailAndPassword(email, password);
        verify(mockTask).addOnCompleteListener(captor.capture());

        // Simulate the task completion
        captor.getValue().onComplete(mockTask);

        // Assert the result
        assertEquals("Registration Successful", expectedResult[0]);
    }
}
