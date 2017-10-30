package ch.ecommunicate.email;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    public MyFirebaseInstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {

        main_thread_function();

    }

    private void main_thread_function() {
        new RegisterDevice().execute();
    }

}