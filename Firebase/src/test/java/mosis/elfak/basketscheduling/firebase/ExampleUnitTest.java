package mosis.elfak.basketscheduling.firebase;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void createUser(){
        String email = "mladen.mladenovic95@yahoo.com";
        String password = "mladen1995";
        FirebaseServices.getInstance().firebaseAuthClient.createUserWithEmailAndPassword(email, password);
    }
}