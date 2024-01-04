import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import androidx.annotation.NonNull;

public class MockBoolPromise implements Promise {
    boolean expectedValue;

    public MockBoolPromise(boolean expectedValue) {
        this.expectedValue = expectedValue;
    }

    public void reject(java.lang.String code, java.lang.String message) {fail();}
    public void reject(java.lang.String code, java.lang.String message, java.lang.Throwable e) {fail();}
    public void reject(java.lang.String code, java.lang.Throwable e) {fail();}
    public void reject(java.lang.Throwable reason) {fail();}
    public void reject(Throwable throwable, WritableMap writableMap) {fail();}
    public void reject(String s, @NonNull WritableMap writableMap) {fail();}
    public void reject(String s, Throwable throwable, WritableMap writableMap) {fail();}
    public void reject(String s, String s1, @NonNull WritableMap writableMap) {fail();}
    public void reject(String s, String s1, Throwable throwable, WritableMap writableMap) {fail();}
    public void reject(String s) {fail();}

    public void resolve(java.lang.Object value) {
        assertEquals(value, expectedValue);
    }

}
